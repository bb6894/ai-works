package com.example.smartagri.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("user")
public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private String displayName;
    private Long roleId;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
