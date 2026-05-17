package com.example.smartagri.controller;

import com.example.smartagri.common.ApiResponse;
import com.example.smartagri.common.OperationLoggable;
import com.example.smartagri.domain.AlarmRule;
import com.example.smartagri.dto.PageResult;
import com.example.smartagri.service.AlarmRuleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rules")
public class AlarmRuleController {
    private final AlarmRuleService alarmRuleService;

    @GetMapping
    public ApiResponse<PageResult<AlarmRule>> page(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(alarmRuleService.page(page, size, keyword));
    }

    @GetMapping("/all")
    public ApiResponse<List<AlarmRule>> all() {
        return ApiResponse.ok(alarmRuleService.listAll());
    }

    @PostMapping
    @OperationLoggable(module = "预警规则", action = "新增规则")
    public ApiResponse<AlarmRule> create(@RequestBody AlarmRule rule) {
        return ApiResponse.ok(alarmRuleService.create(rule));
    }

    @PutMapping("/{id}")
    @OperationLoggable(module = "预警规则", action = "编辑规则")
    public ApiResponse<AlarmRule> update(@PathVariable Long id, @RequestBody AlarmRule rule) {
        return ApiResponse.ok(alarmRuleService.update(id, rule));
    }

    @PutMapping("/{id}/toggle")
    @OperationLoggable(module = "预警规则", action = "启停规则")
    public ApiResponse<AlarmRule> toggle(@PathVariable Long id, @RequestParam boolean enabled) {
        return ApiResponse.ok(alarmRuleService.toggle(id, enabled));
    }

    @DeleteMapping("/{id}")
    @OperationLoggable(module = "预警规则", action = "删除规则")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        alarmRuleService.delete(id);
        return ApiResponse.ok(null);
    }
}
