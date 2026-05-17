package com.example.smartagri.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.smartagri.common.BizException;
import com.example.smartagri.domain.User;
import com.example.smartagri.dto.LoginRequest;
import com.example.smartagri.dto.LoginResponse;
import com.example.smartagri.mapper.UserMapper;
import com.example.smartagri.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.username()));
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BizException(401, "账号不存在或已禁用");
        }
        if (!BCrypt.checkpw(request.password(), user.getPasswordHash())) {
            throw new BizException(401, "用户名或密码错误");
        }
        String token = jwtService.issueToken(user.getId(), user.getUsername());
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getDisplayName());
    }
}
