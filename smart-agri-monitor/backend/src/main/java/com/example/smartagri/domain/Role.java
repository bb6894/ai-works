package com.example.smartagri.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("role")
public class Role {
    private Long id;
    private String roleCode;
    private String roleName;
    private LocalDateTime createdAt;
}
