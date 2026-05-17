package com.example.smartagri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.smartagri.config.SmartAgriProperties;
import com.example.smartagri.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class JwtServiceTest {

    @Test
    void issuesAndParsesTokenWhenRedisContainsToken() {
        SmartAgriProperties properties = new SmartAgriProperties();
        properties.getJwt().setSecret("smart-agri-demo-secret-key-with-at-least-32-bytes");
        properties.getJwt().setTtlMinutes(10);
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(any())).thenReturn("admin");
        JwtService jwtService = new JwtService(properties, redisTemplate);

        String token = jwtService.issueToken(1L, "admin");
        when(valueOperations.get(eq("auth:token:" + token))).thenReturn("admin");
        var user = jwtService.parse(token);

        assertThat(user.userId()).isEqualTo(1L);
        assertThat(user.username()).isEqualTo("admin");
    }
}
