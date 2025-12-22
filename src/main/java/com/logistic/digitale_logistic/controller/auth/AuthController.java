package com.logistic.digitale_logistic.controller.auth;

import com.logistic.digitale_logistic.dto.auth.AuthResponse;
import com.logistic.digitale_logistic.dto.auth.LoginRequest;
import com.logistic.digitale_logistic.dto.RegisterRequest;
import com.logistic.digitale_logistic.entity.User;
import com.logistic.digitale_logistic.service.Auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse res) throws Exception {
        AuthResponse authResponse = authService.login(request, res);
        return ResponseEntity.ok(authResponse);
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
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) throws Exception {
        User user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse res) {

        AuthResponse response = authService.refreshToken(refreshToken, res);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
                @CookieValue(name = "refreshToken") String refreshToken,
                HttpServletResponse res) {

            authService.logout(refreshToken, res);
            return ResponseEntity.ok(Map.of("message" , "logout success"));
        }
    }
