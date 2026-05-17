package com.example.smartagri.controller;

import com.example.smartagri.common.ApiResponse;
import com.example.smartagri.common.OperationLoggable;
import com.example.smartagri.domain.FarmPlot;
import com.example.smartagri.dto.PageResult;
import com.example.smartagri.service.FarmPlotService;
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
@RequestMapping("/api/plots")
public class FarmPlotController {
    private final FarmPlotService farmPlotService;

    @GetMapping
    public ApiResponse<PageResult<FarmPlot>> page(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(farmPlotService.page(page, size, keyword));
    }

    @GetMapping("/all")
    public ApiResponse<List<FarmPlot>> all() {
        return ApiResponse.ok(farmPlotService.listAll());
    }

    @PostMapping
    @OperationLoggable(module = "地块管理", action = "新增地块")
    public ApiResponse<FarmPlot> create(@RequestBody FarmPlot plot) {
        return ApiResponse.ok(farmPlotService.create(plot));
    }

    @PutMapping("/{id}")
    @OperationLoggable(module = "地块管理", action = "编辑地块")
    public ApiResponse<FarmPlot> update(@PathVariable Long id, @RequestBody FarmPlot plot) {
        return ApiResponse.ok(farmPlotService.update(id, plot));
    }

    @DeleteMapping("/{id}")
    @OperationLoggable(module = "地块管理", action = "删除地块")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        farmPlotService.delete(id);
        return ApiResponse.ok(null);
    }
}
