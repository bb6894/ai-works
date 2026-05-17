package com.example.smartagri.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "smart-agri")
public class SmartAgriProperties {
    private Admin admin = new Admin();
    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private Simulator simulator = new Simulator();

    @Data
    public static class Admin {
        private String defaultPassword = "admin123";
    }

    @Data
    public static class Jwt {
        private String secret;
        private long ttlMinutes = 720;
    }

    @Data
    public static class Cors {
        private List<String> allowedOrigins = List.of("http://localhost:5173", "http://localhost:8088");
    }

    @Data
    public static class Simulator {
        private boolean enabled = true;
        private long fixedDelayMs = 10000;
    }
}
