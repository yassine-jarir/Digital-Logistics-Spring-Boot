package com.logistic.digitale_logistic.service.Auth;

import com.logistic.digitale_logistic.dto.RegisterRequest;
import com.logistic.digitale_logistic.entity.User;
import com.logistic.digitale_logistic.enums.Role;
import com.logistic.digitale_logistic.mapper.UserMapper;
import com.logistic.digitale_logistic.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication Service
 *
 * Handles user authentication and registration.
 * With Basic Auth, this service validates credentials but doesn't generate tokens.
 * Authentication is handled directly by Spring Security using HTTP Basic Authentication.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Validates user credentials
     *
     * Note: With Basic Auth, this endpoint is optional as authentication is handled
     * by Spring Security. This can be used for credential validation or health checks.
     *
     * @param email user email
     * @param password user password
     * @return success message with user role
     * @throws Exception if credentials are invalid
     */
    public String login(String email, String password) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Invalid credentials");
        }

        return "Login successful. Use Basic Auth with email and password for API requests. Role: " + user.getRole();
    }

    /**
     * Register a new user
     *
     * @param req registration request containing user details
     * @return created user entity
     * @throws Exception if email already exists
     */
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
}
