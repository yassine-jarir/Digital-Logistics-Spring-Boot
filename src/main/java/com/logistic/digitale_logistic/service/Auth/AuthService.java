package com.logistic.digitale_logistic.service.Auth;

import com.logistic.digitale_logistic.dto.RegisterRequest;
import com.logistic.digitale_logistic.dto.auth.AuthResponse;
import com.logistic.digitale_logistic.dto.auth.LoginRequest;
import com.logistic.digitale_logistic.entity.RefreshToken;
import com.logistic.digitale_logistic.entity.User;
import com.logistic.digitale_logistic.enums.Role;
import com.logistic.digitale_logistic.exceptions.ForbeidenException;
import com.logistic.digitale_logistic.exceptions.InvalidCredentialsException;
import com.logistic.digitale_logistic.exceptions.InvalidRefreshTokenException;
import com.logistic.digitale_logistic.jwt.JwtService;
import com.logistic.digitale_logistic.mapper.UserMapper;
import com.logistic.digitale_logistic.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private  final UserMapper userMapper;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse  login(LoginRequest request , HttpServletResponse res) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = UUID.randomUUID().toString();
        refreshTokenService.create(user, refreshToken);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        res.addCookie(cookie);

        return new AuthResponse(accessToken);
    }

    @Transactional
    public User register(RegisterRequest req) throws Exception {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new Exception("Email already in use");
        }

        User user = userMapper.fromRegisterRequest(req);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setActive(true);
        if (user.getRole() == null) {
            user.setRole(Role.CLIENT);
        }

        return userRepository.save(user);
    }

    public AuthResponse refreshToken(String refreshToken, HttpServletResponse res) {

        if (refreshToken == null) {
            throw new RuntimeException("Refresh token missing");
        }

        RefreshToken oldToken = refreshTokenService.validate(refreshToken);
        User user = oldToken.getUser();

        refreshTokenService.revoke(oldToken);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = UUID.randomUUID().toString();

        refreshTokenService.create(user, newRefreshToken);

        Cookie cookie = new Cookie("refreshToken", newRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        res.addCookie(cookie);

        return new AuthResponse(newAccessToken);
    }
    public void logout(String refreshToken, HttpServletResponse res) {
        if (refreshToken != null) {
            RefreshToken token = refreshTokenService.validate(refreshToken);
            refreshTokenService.revoke(token);
        }

        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        res.addCookie(cookie);
    }
}
