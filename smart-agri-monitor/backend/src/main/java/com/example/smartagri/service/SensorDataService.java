package com.example.smartagri.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.smartagri.common.BizException;
import com.example.smartagri.domain.Device;
import com.example.smartagri.domain.SensorData;
import com.example.smartagri.dto.SensorDataRequest;
import com.example.smartagri.mapper.DeviceMapper;
import com.example.smartagri.mapper.SensorDataMapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SensorDataService {
    private final SensorDataMapper sensorDataMapper;
    private final DeviceMapper deviceMapper;
    private final DeviceService deviceService;
    private final AlarmEngine alarmEngine;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public SensorData ingest(SensorDataRequest request) {
        Device device = deviceMapper.selectById(request.deviceId());
        if (device == null) {
            throw new BizException("设备不存在: " + request.deviceId());
        }
        LocalDateTime collectedAt = request.collectedAt() == null ? LocalDateTime.now() : request.collectedAt();
        SensorData data = new SensorData();
        data.setDeviceId(device.getId());
        data.setPlotId(device.getPlotId());
        data.setMetricType(request.metricType());
        data.setMetricValue(request.metricValue());
        data.setUnit(request.unit());
        data.setCollectedAt(collectedAt);
        data.setCreatedAt(LocalDateTime.now());
        sensorDataMapper.insert(data);
        deviceService.markSeen(device.getId());
        cacheLatest(data);
        alarmEngine.evaluate(data);
        return data;
    }

    public List<SensorData> latest(Long deviceId) {
        if (deviceId != null) {
            List<SensorData> cached = cachedLatest(deviceId);
            if (!cached.isEmpty()) {
                return cached;
            }
        }
        LambdaQueryWrapper<SensorData> wrapper = new LambdaQueryWrapper<SensorData>()
                .orderByDesc(SensorData::getCollectedAt)
                .last("limit 40");
        if (deviceId != null) {
            wrapper.eq(SensorData::getDeviceId, deviceId);
        }
        return sensorDataMapper.selectList(wrapper);
    }

    public List<SensorData> history(Long deviceId, String metricType, LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<SensorData> wrapper = new LambdaQueryWrapper<SensorData>()
                .eq(deviceId != null, SensorData::getDeviceId, deviceId)
                .eq(metricType != null && !metricType.isBlank(), SensorData::getMetricType, metricType)
                .ge(start != null, SensorData::getCollectedAt, start)
                .le(end != null, SensorData::getCollectedAt, end)
                .orderByAsc(SensorData::getCollectedAt);
        return sensorDataMapper.selectList(wrapper);
    }

    private void cacheLatest(SensorData data) {
        redisTemplate.opsForHash().put(latestKey(data.getDeviceId()), data.getMetricType(), data);
    }

    private List<SensorData> cachedLatest(Long deviceId) {
        return redisTemplate.opsForHash().values(latestKey(deviceId)).stream()
                .filter(SensorData.class::isInstance)
                .map(SensorData.class::cast)
                .toList();
    }

    private String latestKey(Long deviceId) {
        return "device:latest:" + deviceId;
    }
}
