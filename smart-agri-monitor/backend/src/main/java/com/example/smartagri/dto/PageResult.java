package com.example.smartagri.dto;

import java.util.List;

public record PageResult<T>(
        long total,
        List<T> records
) {
}
