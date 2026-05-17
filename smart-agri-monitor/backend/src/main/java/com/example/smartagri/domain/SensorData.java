package com.example.smartagri.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("sensor_data")
public class SensorData {
    private Long id;
    private Long deviceId;
    private Long plotId;
    private String metricType;
    private BigDecimal metricValue;
    private String unit;
    private LocalDateTime collectedAt;
    private LocalDateTime createdAt;
}
