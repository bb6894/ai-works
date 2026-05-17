package com.example.smartagri.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("operation_log")
public class OperationLog {
    private Long id;
    private Long userId;
    private String username;
    private String moduleName;
    private String actionName;
    private String requestMethod;
    private String requestUri;
    private String ipAddress;
    private Integer success;
    private String errorMessage;
    private LocalDateTime createdAt;
}
