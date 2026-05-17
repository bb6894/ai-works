package com.example.smartagri.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smartagri.domain.AlarmRule;
import com.example.smartagri.mapper.AlarmRuleMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmRuleService extends CrudService<AlarmRule> {
    private final AlarmRuleMapper alarmRuleMapper;

    @Override
    protected BaseMapper<AlarmRule> mapper() {
        return alarmRuleMapper;
    }

    @Override
    protected LambdaQueryWrapper<AlarmRule> query(String keyword) {
        LambdaQueryWrapper<AlarmRule> wrapper = new LambdaQueryWrapper<AlarmRule>()
                .orderByDesc(AlarmRule::getId);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(AlarmRule::getRuleName, keyword)
                    .or()
                    .like(AlarmRule::getMetricType, keyword);
        }
        return wrapper;
    }

    @Override
    public AlarmRule create(AlarmRule entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        if (entity.getEnabled() == null) {
            entity.setEnabled(1);
        }
        if (entity.getAlarmLevel() == null || entity.getAlarmLevel().isBlank()) {
            entity.setAlarmLevel("warning");
        }
        return super.create(entity);
    }

    @Override
    public AlarmRule update(Long id, AlarmRule entity) {
        entity.setUpdatedAt(LocalDateTime.now());
        return super.update(id, entity);
    }

    public AlarmRule toggle(Long id, boolean enabled) {
        AlarmRule rule = new AlarmRule();
        rule.setId(id);
        rule.setEnabled(enabled ? 1 : 0);
        rule.setUpdatedAt(LocalDateTime.now());
        alarmRuleMapper.updateById(rule);
        return get(id);
    }

    @Override
    protected void setId(AlarmRule entity, Long id) {
        entity.setId(id);
    }
}
