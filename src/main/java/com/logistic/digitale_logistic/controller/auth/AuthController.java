package com.logistic.digitale_logistic.controller.auth;

import com.logistic.digitale_logistic.dto.LoginRequest;
import com.logistic.digitale_logistic.dto.RegisterRequest;
import com.logistic.digitale_logistic.entity.User;
import com.logistic.digitale_logistic.service.Auth.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) throws Exception {
        String token = authService.login(request.getEmail(), request.getPassword());
        return Map.of("token", token);
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) throws Exception {
        return authService.register(request);
    }
}
