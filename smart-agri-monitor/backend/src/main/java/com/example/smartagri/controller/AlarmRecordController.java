package com.example.smartagri.controller;

import com.example.smartagri.common.ApiResponse;
import com.example.smartagri.common.OperationLoggable;
import com.example.smartagri.domain.AlarmRecord;
import com.example.smartagri.dto.AlarmHandleRequest;
import com.example.smartagri.dto.PageResult;
import com.example.smartagri.service.AlarmRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
public class AlarmRecordController {
    private final AlarmRecordService alarmRecordService;

    @GetMapping
    public ApiResponse<PageResult<AlarmRecord>> page(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(alarmRecordService.page(page, size, keyword));
    }

    @PutMapping("/{id}/handle")
    @OperationLoggable(module = "告警处理", action = "处理告警")
    public ApiResponse<AlarmRecord> handle(@PathVariable Long id, @Valid @RequestBody AlarmHandleRequest request) {
        return ApiResponse.ok(alarmRecordService.handle(id, request));
    }
}
