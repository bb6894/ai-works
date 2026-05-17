package com.example.smartagri.controller;

import com.example.smartagri.common.OperationLoggable;
import com.example.smartagri.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/sensor-data")
    @OperationLoggable(module = "报表导出", action = "导出传感器数据")
    public void exportSensorData(HttpServletResponse response,
                                 @RequestParam(required = false) Long deviceId,
                                 @RequestParam(required = false) String metricType,
                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) throws Exception {
        reportService.exportSensorData(response, deviceId, metricType, start, end);
    }

    @GetMapping("/alarms")
    @OperationLoggable(module = "报表导出", action = "导出告警记录")
    public void exportAlarms(HttpServletResponse response) throws Exception {
        reportService.exportAlarms(response);
    }
}
