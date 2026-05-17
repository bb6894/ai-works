package com.example.smartagri.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("alarm_rule")
public class AlarmRule {
    private Long id;
    private String ruleName;
    private Long plotId;
    private Long deviceId;
    private String metricType;
    private String compareOperator;
    private BigDecimal thresholdValue;
    private BigDecimal thresholdValue2;
    private String alarmLevel;
    private Integer enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
