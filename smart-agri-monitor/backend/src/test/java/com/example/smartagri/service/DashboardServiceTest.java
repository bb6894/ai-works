package com.example.smartagri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.smartagri.mapper.AlarmRecordMapper;
import com.example.smartagri.mapper.DeviceMapper;
import com.example.smartagri.mapper.SensorDataMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class DashboardServiceTest {

    @Test
    void calculatesDashboardCounts() {
        DeviceMapper deviceMapper = mock(DeviceMapper.class);
        SensorDataMapper sensorDataMapper = mock(SensorDataMapper.class);
        AlarmRecordMapper alarmRecordMapper = mock(AlarmRecordMapper.class);
        @SuppressWarnings("unchecked")
        RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(deviceMapper.selectCount(any())).thenReturn(2L, 1L);
        when(sensorDataMapper.selectCount(any())).thenReturn(12L);
        when(alarmRecordMapper.selectCount(any())).thenReturn(3L);
        when(alarmRecordMapper.selectList(any())).thenReturn(List.of());
        when(sensorDataMapper.selectList(any())).thenReturn(List.of());

        DashboardService service = new DashboardService(deviceMapper, sensorDataMapper, alarmRecordMapper, redisTemplate);
        var stats = service.stats();

        assertThat(stats.deviceTotal()).isEqualTo(2);
        assertThat(stats.onlineDeviceTotal()).isEqualTo(1);
        assertThat(stats.todayDataTotal()).isEqualTo(12);
        assertThat(stats.pendingAlarmTotal()).isEqualTo(3);
    }
}
