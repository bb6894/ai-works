package com.example.smartagri;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan("com.example.smartagri.mapper")
@SpringBootApplication
public class SmartAgriApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartAgriApplication.class, args);
    }
}
