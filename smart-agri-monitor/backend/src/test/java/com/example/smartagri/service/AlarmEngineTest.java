package com.example.smartagri.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.smartagri.domain.AlarmRule;
import com.example.smartagri.domain.SensorData;
import java.math.BigDecimal;
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
        data.setMetricValue(new BigDecimal(value));
        return data;
    }
}
