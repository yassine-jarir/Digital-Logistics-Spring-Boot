package com.logistic.digitale_logistic.controller.auth;

import com.logistic.digitale_logistic.dto.LoginRequest;
import com.logistic.digitale_logistic.dto.RegisterRequest;
import com.logistic.digitale_logistic.entity.User;
import com.logistic.digitale_logistic.service.Auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "User login",
            description = "Authenticate user with email and password, returns JWT token for subsequent requests"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) throws Exception {
        String token = authService.login(request.getEmail(), request.getPassword());
        return Map.of("token", token);
    }

    @Operation(
            summary = "User registration",
            description = "Register a new user with email, password, name and role (CLIENT, WAREHOUSE_MANAGER, ADMIN)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration successful",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid registration data or email already exists")
    })
    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) throws Exception {
        return authService.register(request);
    }
}
