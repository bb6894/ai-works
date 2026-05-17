package com.example.smartagri.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.smartagri.config.SmartAgriProperties;
import com.example.smartagri.domain.Device;
import com.example.smartagri.dto.SensorDataRequest;
import com.example.smartagri.mapper.DeviceMapper;
import com.example.smartagri.service.SensorDataService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SensorSimulatorJob {
    private final SmartAgriProperties properties;
    private final DeviceMapper deviceMapper;
    private final SensorDataService sensorDataService;

    @Scheduled(fixedDelayString = "${smart-agri.simulator.fixed-delay-ms:10000}")
    public void generate() {
        if (!properties.getSimulator().isEnabled()) {
            return;
        }
        List<Device> devices = deviceMapper.selectList(new LambdaQueryWrapper<Device>()
                .eq(Device::getOnlineStatus, 1)
                .last("limit 20"));
        for (Device device : devices) {
            emit(device.getId(), "temperature", 18, 38, "C");
            emit(device.getId(), "air_humidity", 40, 95, "%");
            emit(device.getId(), "soil_moisture", 18, 80, "%");
            emit(device.getId(), "light", 2000, 90000, "lux");
        }
    }

    private void emit(Long deviceId, String metricType, double min, double max, String unit) {
        BigDecimal value = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(min, max))
                .setScale(2, RoundingMode.HALF_UP);
        sensorDataService.ingest(new SensorDataRequest(deviceId, metricType, value, unit, LocalDateTime.now()));
    }
}
