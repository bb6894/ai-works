package com.example.smartagri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.smartagri.common.BizException;
import com.example.smartagri.domain.Device;
import com.example.smartagri.domain.SensorData;
import com.example.smartagri.dto.SensorDataRequest;
import com.example.smartagri.mapper.DeviceMapper;
import com.example.smartagri.mapper.SensorDataMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

class SensorDataServiceTest {

    @Test
    void ingestPersistsCachesAndEvaluatesSensorData() {
        SensorDataMapper sensorDataMapper = mock(SensorDataMapper.class);
        DeviceMapper deviceMapper = mock(DeviceMapper.class);
        DeviceService deviceService = mock(DeviceService.class);
        AlarmEngine alarmEngine = mock(AlarmEngine.class);
        @SuppressWarnings("unchecked")
        RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        @SuppressWarnings("unchecked")
        HashOperations<String, Object, Object> hashOperations = mock(HashOperations.class);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        Device device = new Device();
        device.setId(3L);
        device.setPlotId(9L);
        when(deviceMapper.selectById(3L)).thenReturn(device);
        SensorDataService service = new SensorDataService(
                sensorDataMapper, deviceMapper, deviceService, alarmEngine, redisTemplate);
        LocalDateTime collectedAt = LocalDateTime.of(2026, 5, 21, 8, 30);

        SensorData data = service.ingest(new SensorDataRequest(
                3L, "temperature", new BigDecimal("32.5"), "C", collectedAt));

        assertThat(data.getDeviceId()).isEqualTo(3L);
        assertThat(data.getPlotId()).isEqualTo(9L);
        assertThat(data.getMetricType()).isEqualTo("temperature");
        assertThat(data.getMetricValue()).isEqualByComparingTo("32.5");
        assertThat(data.getCollectedAt()).isEqualTo(collectedAt);
        assertThat(data.getCreatedAt()).isNotNull();
        verify(sensorDataMapper).insert(data);
        verify(deviceService).markSeen(3L);
        verify(hashOperations).put("device:latest:3", "temperature", data);
        verify(alarmEngine).evaluate(data);
    }

    @Test
    void ingestRejectsUnknownDevice() {
        SensorDataMapper sensorDataMapper = mock(SensorDataMapper.class);
        DeviceMapper deviceMapper = mock(DeviceMapper.class);
        DeviceService deviceService = mock(DeviceService.class);
        AlarmEngine alarmEngine = mock(AlarmEngine.class);
        @SuppressWarnings("unchecked")
        RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        when(deviceMapper.selectById(404L)).thenReturn(null);
        SensorDataService service = new SensorDataService(
                sensorDataMapper, deviceMapper, deviceService, alarmEngine, redisTemplate);

        assertThatThrownBy(() -> service.ingest(new SensorDataRequest(
                        404L, "temperature", new BigDecimal("32.5"), "C", null)))
                .isInstanceOf(BizException.class)
                .hasMessage("设备不存在: 404");
    }
}
