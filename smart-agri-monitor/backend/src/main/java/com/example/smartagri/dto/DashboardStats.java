package com.example.smartagri.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardStats(
        long deviceTotal,
        long onlineDeviceTotal,
        long todayDataTotal,
        long pendingAlarmTotal,
        List<Map<String, Object>> alarmTrend,
        List<Map<String, Object>> deviceStatus,
        List<Map<String, Object>> metricTrend,
        Map<String, BigDecimal> latestMetrics
) {
}
