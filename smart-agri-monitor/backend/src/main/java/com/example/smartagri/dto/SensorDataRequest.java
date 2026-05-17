package com.example.smartagri.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SensorDataRequest(
        @NotNull Long deviceId,
        @NotBlank String metricType,
        @NotNull @DecimalMin("-100000") BigDecimal metricValue,
        String unit,
        LocalDateTime collectedAt
) {
}
