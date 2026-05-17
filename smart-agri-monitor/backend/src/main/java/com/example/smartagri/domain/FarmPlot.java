package com.example.smartagri.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("farm_plot")
public class FarmPlot {
    private Long id;
    private String name;
    private String location;
    private BigDecimal areaMu;
    private String cropType;
    private String manager;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
