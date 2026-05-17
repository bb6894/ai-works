package com.example.smartagri.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.smartagri.domain.User;
import com.example.smartagri.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DemoDataInitializer implements ApplicationRunner {
    private static final String HASH_PLACEHOLDER = "{bcrypt-on-startup}";

    private final UserMapper userMapper;
    private final SmartAgriProperties properties;

    @Override
    public void run(ApplicationArguments args) {
        User admin = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, "admin"));
        if (admin != null && HASH_PLACEHOLDER.equals(admin.getPasswordHash())) {
            User patch = new User();
            patch.setId(admin.getId());
            patch.setPasswordHash(BCrypt.hashpw(properties.getAdmin().getDefaultPassword(), BCrypt.gensalt()));
            userMapper.updateById(patch);
        }
    }
}
