package com.example.smartagri.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("device")
public class Device {
    private Long id;
    private String deviceCode;
    private String deviceName;
    private Long plotId;
    private String deviceType;
    private Integer onlineStatus;
    private LocalDate installedAt;
    private LocalDateTime lastSeenAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
