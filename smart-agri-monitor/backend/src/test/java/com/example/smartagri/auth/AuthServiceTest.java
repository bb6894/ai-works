package com.example.smartagri.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.smartagri.common.BizException;
import com.example.smartagri.domain.User;
import com.example.smartagri.dto.LoginRequest;
import com.example.smartagri.mapper.UserMapper;
import com.example.smartagri.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

class AuthServiceTest {

    @Test
    void loginIssuesTokenForActiveUserWithValidPassword() {
        UserMapper userMapper = mock(UserMapper.class);
        JwtService jwtService = mock(JwtService.class);
        User user = activeUser();
        when(userMapper.selectOne(any())).thenReturn(user);
        when(jwtService.issueToken(7L, "admin")).thenReturn("jwt-token");
        AuthService service = new AuthService(userMapper, jwtService);

        var response = service.login(new LoginRequest("admin", "admin123"));

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.userId()).isEqualTo(7L);
        assertThat(response.username()).isEqualTo("admin");
        assertThat(response.displayName()).isEqualTo("系统管理员");
        verify(jwtService).issueToken(7L, "admin");
    }

    @Test
    void loginRejectsInvalidPassword() {
        UserMapper userMapper = mock(UserMapper.class);
        JwtService jwtService = mock(JwtService.class);
        when(userMapper.selectOne(any())).thenReturn(activeUser());
        AuthService service = new AuthService(userMapper, jwtService);

        assertThatThrownBy(() -> service.login(new LoginRequest("admin", "wrong-password")))
                .isInstanceOf(BizException.class)
                .hasMessage("用户名或密码错误")
                .extracting("code")
                .isEqualTo(401);
    }

    @Test
    void loginRejectsDisabledUser() {
        UserMapper userMapper = mock(UserMapper.class);
        JwtService jwtService = mock(JwtService.class);
        User user = activeUser();
        user.setStatus(0);
        when(userMapper.selectOne(any())).thenReturn(user);
        AuthService service = new AuthService(userMapper, jwtService);

        assertThatThrownBy(() -> service.login(new LoginRequest("admin", "admin123")))
                .isInstanceOf(BizException.class)
                .hasMessage("账号不存在或已禁用")
                .extracting("code")
                .isEqualTo(401);
    }

    private User activeUser() {
        User user = new User();
        user.setId(7L);
        user.setUsername("admin");
        user.setDisplayName("系统管理员");
        user.setStatus(1);
        user.setPasswordHash(BCrypt.hashpw("admin123", BCrypt.gensalt()));
        return user;
    }
}
