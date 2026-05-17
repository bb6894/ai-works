package com.example.smartagri.controller;

import com.example.smartagri.common.ApiResponse;
import com.example.smartagri.common.OperationLoggable;
import com.example.smartagri.domain.Device;
import com.example.smartagri.dto.PageResult;
import com.example.smartagri.service.DeviceService;
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
@RequestMapping("/api/devices")
public class DeviceController {
    private final DeviceService deviceService;

    @GetMapping
    public ApiResponse<PageResult<Device>> page(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(deviceService.page(page, size, keyword));
    }

    @GetMapping("/all")
    public ApiResponse<List<Device>> all() {
        return ApiResponse.ok(deviceService.listAll());
    }

    @PostMapping
    @OperationLoggable(module = "设备管理", action = "新增设备")
    public ApiResponse<Device> create(@RequestBody Device device) {
        return ApiResponse.ok(deviceService.create(device));
    }

    @PutMapping("/{id}")
    @OperationLoggable(module = "设备管理", action = "编辑设备")
    public ApiResponse<Device> update(@PathVariable Long id, @RequestBody Device device) {
        return ApiResponse.ok(deviceService.update(id, device));
    }

    @DeleteMapping("/{id}")
    @OperationLoggable(module = "设备管理", action = "删除设备")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        deviceService.delete(id);
        return ApiResponse.ok(null);
    }
}
