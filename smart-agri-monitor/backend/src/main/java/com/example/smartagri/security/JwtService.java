package com.example.smartagri.security;

import com.example.smartagri.config.SmartAgriProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final SmartAgriProperties properties;
    private final StringRedisTemplate redisTemplate;

    public String issueToken(Long userId, String username) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(properties.getJwt().getTtlMinutes() * 60);
        String token = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(key())
                .compact();
        redisTemplate.opsForValue().set(tokenKey(token), username, properties.getJwt().getTtlMinutes(), java.util.concurrent.TimeUnit.MINUTES);
        return token;
    }

    public JwtUser parse(String token) {
        var claims = Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String cached = redisTemplate.opsForValue().get(tokenKey(token));
        if (cached == null) {
            throw new IllegalArgumentException("token 已过期或已退出");
        }
        return new JwtUser(Long.valueOf(claims.getSubject()), claims.get("username", String.class));
    }

    public void revoke(String token) {
        redisTemplate.delete(tokenKey(token));
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
    }

    private String tokenKey(String token) {
        return "auth:token:" + token;
    }
}
