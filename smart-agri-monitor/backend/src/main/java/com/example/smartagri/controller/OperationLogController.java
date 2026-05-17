package com.example.smartagri.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.smartagri.common.ApiResponse;
import com.example.smartagri.domain.OperationLog;
import com.example.smartagri.dto.PageResult;
import com.example.smartagri.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/operation-logs")
public class OperationLogController {
    private final OperationLogMapper operationLogMapper;

    @GetMapping
    public ApiResponse<PageResult<OperationLog>> page(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<OperationLog>()
                .orderByDesc(OperationLog::getCreatedAt);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(OperationLog::getModuleName, keyword)
                    .or()
                    .like(OperationLog::getActionName, keyword)
                    .or()
                    .like(OperationLog::getUsername, keyword);
        }
        Page<OperationLog> result = operationLogMapper.selectPage(Page.of(page, size), wrapper);
        return ApiResponse.ok(new PageResult<>(result.getTotal(), result.getRecords()));
    }
}
