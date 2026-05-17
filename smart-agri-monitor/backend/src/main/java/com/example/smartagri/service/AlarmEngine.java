package com.example.smartagri.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.smartagri.domain.AlarmRecord;
import com.example.smartagri.domain.AlarmRule;
import com.example.smartagri.domain.SensorData;
import com.example.smartagri.mapper.AlarmRecordMapper;
import com.example.smartagri.mapper.AlarmRuleMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmEngine {
    private final AlarmRuleMapper alarmRuleMapper;
    private final AlarmRecordMapper alarmRecordMapper;

    public boolean matches(AlarmRule rule, SensorData data) {
        BigDecimal value = data.getMetricValue();
        BigDecimal threshold = rule.getThresholdValue();
        BigDecimal threshold2 = rule.getThresholdValue2();
        return switch (rule.getCompareOperator()) {
            case "GT" -> value.compareTo(threshold) > 0;
            case "GTE" -> value.compareTo(threshold) >= 0;
            case "LT" -> value.compareTo(threshold) < 0;
            case "LTE" -> value.compareTo(threshold) <= 0;
            case "OUTSIDE" -> threshold2 != null
                    && (value.compareTo(threshold) < 0 || value.compareTo(threshold2) > 0);
            default -> false;
        };
    }

    public void evaluate(SensorData data) {
        List<AlarmRule> rules = alarmRuleMapper.selectList(new LambdaQueryWrapper<AlarmRule>()
                .eq(AlarmRule::getEnabled, 1)
                .eq(AlarmRule::getMetricType, data.getMetricType())
                .and(w -> w.isNull(AlarmRule::getDeviceId).or().eq(AlarmRule::getDeviceId, data.getDeviceId()))
                .and(w -> w.isNull(AlarmRule::getPlotId).or().eq(AlarmRule::getPlotId, data.getPlotId())));

        for (AlarmRule rule : rules) {
            if (matches(rule, data) && !hasOpenAlarm(rule.getId(), data.getDeviceId())) {
                alarmRecordMapper.insert(toAlarmRecord(rule, data));
            }
        }
    }

    private boolean hasOpenAlarm(Long ruleId, Long deviceId) {
        Long count = alarmRecordMapper.selectCount(new LambdaQueryWrapper<AlarmRecord>()
                .eq(AlarmRecord::getRuleId, ruleId)
                .eq(AlarmRecord::getDeviceId, deviceId)
                .in(AlarmRecord::getStatus, "pending", "processing"));
        return count != null && count > 0;
    }

    private AlarmRecord toAlarmRecord(AlarmRule rule, SensorData data) {
        LocalDateTime now = LocalDateTime.now();
        AlarmRecord record = new AlarmRecord();
        record.setRuleId(rule.getId());
        record.setDeviceId(data.getDeviceId());
        record.setPlotId(data.getPlotId());
        record.setMetricType(data.getMetricType());
        record.setMetricValue(data.getMetricValue());
        record.setAlarmLevel(rule.getAlarmLevel());
        record.setStatus("pending");
        record.setMessage(rule.getRuleName() + " 触发: " + data.getMetricType() + "=" + data.getMetricValue());
        record.setTriggeredAt(data.getCollectedAt());
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        return record;
    }
}
