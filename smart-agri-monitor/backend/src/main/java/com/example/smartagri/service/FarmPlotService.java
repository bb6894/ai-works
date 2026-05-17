package com.example.smartagri.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smartagri.domain.FarmPlot;
import com.example.smartagri.mapper.FarmPlotMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FarmPlotService extends CrudService<FarmPlot> {
    private final FarmPlotMapper farmPlotMapper;

    @Override
    protected BaseMapper<FarmPlot> mapper() {
        return farmPlotMapper;
    }

    @Override
    protected LambdaQueryWrapper<FarmPlot> query(String keyword) {
        LambdaQueryWrapper<FarmPlot> wrapper = new LambdaQueryWrapper<FarmPlot>()
                .orderByDesc(FarmPlot::getId);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(FarmPlot::getName, keyword)
                    .or()
                    .like(FarmPlot::getLocation, keyword)
                    .or()
                    .like(FarmPlot::getCropType, keyword);
        }
        return wrapper;
    }

    @Override
    public FarmPlot create(FarmPlot entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        return super.create(entity);
    }

    @Override
    public FarmPlot update(Long id, FarmPlot entity) {
        entity.setUpdatedAt(LocalDateTime.now());
        return super.update(id, entity);
    }

    @Override
    protected void setId(FarmPlot entity, Long id) {
        entity.setId(id);
    }
}
