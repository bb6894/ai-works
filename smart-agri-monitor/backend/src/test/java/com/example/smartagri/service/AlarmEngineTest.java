package com.example.smartagri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.smartagri.domain.AlarmRule;
import com.example.smartagri.domain.AlarmRecord;
import com.example.smartagri.domain.SensorData;
import com.example.smartagri.mapper.AlarmRecordMapper;
import com.example.smartagri.mapper.AlarmRuleMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class AlarmEngineTest {

    private final AlarmEngine alarmEngine = new AlarmEngine(null, null);

    @Test
    void matchesGreaterThanRule() {
        AlarmRule rule = rule("GT", "30", null);
        SensorData data = data("31.5");

        assertThat(alarmEngine.matches(rule, data)).isTrue();
    }

    @Test
    void doesNotMatchLessThanRuleWhenValueAboveThreshold() {
        AlarmRule rule = rule("LT", "35", null);
        SensorData data = data("40");

        assertThat(alarmEngine.matches(rule, data)).isFalse();
    }

    @Test
    void matchesOutsideRangeRule() {
        AlarmRule rule = rule("OUTSIDE", "20", "80");

        assertThat(alarmEngine.matches(rule, data("10"))).isTrue();
        assertThat(alarmEngine.matches(rule, data("90"))).isTrue();
        assertThat(alarmEngine.matches(rule, data("50"))).isFalse();
    }

    @Test
    void evaluateCreatesAlarmWhenRuleMatchesAndNoAlarmIsOpen() {
        AlarmRuleMapper alarmRuleMapper = mock(AlarmRuleMapper.class);
        AlarmRecordMapper alarmRecordMapper = mock(AlarmRecordMapper.class);
        AlarmRule rule = rule("GTE", "30", null);
        rule.setId(4L);
        rule.setRuleName("高温预警");
        rule.setAlarmLevel("warning");
        rule.setMetricType("temperature");
        when(alarmRuleMapper.selectList(any())).thenReturn(List.of(rule));
        when(alarmRecordMapper.selectCount(any())).thenReturn(0L);
        AlarmEngine engine = new AlarmEngine(alarmRuleMapper, alarmRecordMapper);

        engine.evaluate(data("31.5"));

        verify(alarmRecordMapper).insert(any(AlarmRecord.class));
    }

    @Test
    void evaluateDoesNotCreateDuplicateOpenAlarm() {
        AlarmRuleMapper alarmRuleMapper = mock(AlarmRuleMapper.class);
        AlarmRecordMapper alarmRecordMapper = mock(AlarmRecordMapper.class);
        AlarmRule rule = rule("GTE", "30", null);
        rule.setId(4L);
        rule.setRuleName("高温预警");
        rule.setAlarmLevel("warning");
        rule.setMetricType("temperature");
        when(alarmRuleMapper.selectList(any())).thenReturn(List.of(rule));
        when(alarmRecordMapper.selectCount(any())).thenReturn(1L);
        AlarmEngine engine = new AlarmEngine(alarmRuleMapper, alarmRecordMapper);

        engine.evaluate(data("31.5"));

        verify(alarmRecordMapper, never()).insert(any(AlarmRecord.class));
    }

    private AlarmRule rule(String operator, String threshold, String threshold2) {
        AlarmRule rule = new AlarmRule();
        rule.setCompareOperator(operator);
        rule.setThresholdValue(new BigDecimal(threshold));
        if (threshold2 != null) {
            rule.setThresholdValue2(new BigDecimal(threshold2));
        }
        return rule;
    }

    private SensorData data(String value) {
        SensorData data = new SensorData();
        data.setDeviceId(1L);
        data.setPlotId(2L);
        data.setMetricType("temperature");
        data.setMetricValue(new BigDecimal(value));
        data.setCollectedAt(LocalDateTime.of(2026, 5, 21, 8, 30));
        return data;
    }
}
