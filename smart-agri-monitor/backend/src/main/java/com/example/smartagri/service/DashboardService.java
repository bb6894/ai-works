package com.example.smartagri.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.smartagri.domain.AlarmRecord;
import com.example.smartagri.domain.Device;
import com.example.smartagri.domain.SensorData;
import com.example.smartagri.dto.DashboardStats;
import com.example.smartagri.mapper.AlarmRecordMapper;
import com.example.smartagri.mapper.DeviceMapper;
import com.example.smartagri.mapper.SensorDataMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DeviceMapper deviceMapper;
    private final SensorDataMapper sensorDataMapper;
    private final AlarmRecordMapper alarmRecordMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public DashboardStats stats() {
        Object cached = redisTemplate.opsForValue().get("dashboard:stats");
        if (cached instanceof DashboardStats stats) {
            return stats;
        }

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        long deviceTotal = deviceMapper.selectCount(new LambdaQueryWrapper<>());
        long onlineDeviceTotal = deviceMapper.selectCount(new LambdaQueryWrapper<Device>()
                .eq(Device::getOnlineStatus, 1));
        long todayDataTotal = sensorDataMapper.selectCount(new LambdaQueryWrapper<SensorData>()
                .ge(SensorData::getCollectedAt, startOfDay));
        long pendingAlarmTotal = alarmRecordMapper.selectCount(new LambdaQueryWrapper<AlarmRecord>()
                .in(AlarmRecord::getStatus, "pending", "processing"));

        List<AlarmRecord> recentAlarms = alarmRecordMapper.selectList(new LambdaQueryWrapper<AlarmRecord>()
                .ge(AlarmRecord::getTriggeredAt, LocalDate.now().minusDays(6).atStartOfDay()));
        List<Map<String, Object>> alarmTrend = recentAlarms.stream()
                .collect(Collectors.groupingBy(a -> a.getTriggeredAt().toLocalDate().toString(), LinkedHashMap::new, Collectors.counting()))
                .entrySet().stream()
                .map(e -> Map.<String, Object>of("date", e.getKey(), "count", e.getValue()))
                .toList();

        List<Map<String, Object>> deviceStatus = List.of(
                Map.of("name", "在线", "value", onlineDeviceTotal),
                Map.of("name", "离线", "value", Math.max(0, deviceTotal - onlineDeviceTotal))
        );

        List<SensorData> recentData = sensorDataMapper.selectList(new LambdaQueryWrapper<SensorData>()
                .orderByDesc(SensorData::getCollectedAt)
                .last("limit 80"));
        List<Map<String, Object>> metricTrend = recentData.stream()
                .sorted((a, b) -> a.getCollectedAt().compareTo(b.getCollectedAt()))
                .map(d -> Map.<String, Object>of(
                        "time", d.getCollectedAt().toString(),
                        "metricType", d.getMetricType(),
                        "value", d.getMetricValue()))
                .toList();
        Map<String, BigDecimal> latestMetrics = recentData.stream()
                .collect(Collectors.toMap(
                        SensorData::getMetricType,
                        SensorData::getMetricValue,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        DashboardStats stats = new DashboardStats(
                deviceTotal,
                onlineDeviceTotal,
                todayDataTotal,
                pendingAlarmTotal,
                alarmTrend,
                deviceStatus,
                metricTrend,
                latestMetrics
        );
        redisTemplate.opsForValue().set("dashboard:stats", stats, java.time.Duration.ofSeconds(10));
        return stats;
    }
}
