package com.example.smartagri.controller;

import com.example.smartagri.common.ApiResponse;
import com.example.smartagri.domain.SensorData;
import com.example.smartagri.dto.SensorDataRequest;
import com.example.smartagri.service.SensorDataService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sensor-data")
public class SensorDataController {
    private final SensorDataService sensorDataService;

    @PostMapping
    public ApiResponse<SensorData> ingest(@Valid @RequestBody SensorDataRequest request) {
        return ApiResponse.ok(sensorDataService.ingest(request));
    }

    @GetMapping("/latest")
    public ApiResponse<List<SensorData>> latest(@RequestParam(required = false) Long deviceId) {
        return ApiResponse.ok(sensorDataService.latest(deviceId));
    }

    @GetMapping("/history")
    public ApiResponse<List<SensorData>> history(
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) String metricType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) {
        return ApiResponse.ok(sensorDataService.history(deviceId, metricType, start, end));
    }
}
