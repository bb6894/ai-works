package com.example.smartagri.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("alarm_record")
public class AlarmRecord {
    private Long id;
    private Long ruleId;
    private Long deviceId;
    private Long plotId;
    private String metricType;
    private BigDecimal metricValue;
    private String alarmLevel;
    private String status;
    private String message;
    private String handler;
    private String handleRemark;
    private LocalDateTime handledAt;
    private LocalDateTime triggeredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
