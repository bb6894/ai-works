package com.example.smartagri.dto;

import jakarta.validation.constraints.NotBlank;

public record AlarmHandleRequest(
        @NotBlank String status,
        String remark
) {
}
