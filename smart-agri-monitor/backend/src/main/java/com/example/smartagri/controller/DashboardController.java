package com.example.smartagri.controller;

import com.example.smartagri.common.ApiResponse;
import com.example.smartagri.dto.DashboardStats;
import com.example.smartagri.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ApiResponse<DashboardStats> stats() {
        return ApiResponse.ok(dashboardService.stats());
    }
}
