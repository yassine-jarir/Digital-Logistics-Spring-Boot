package com.logistic.digitale_logistic.service.Auth;

import com.logistic.digitale_logistic.dto.RegisterRequest;
import com.logistic.digitale_logistic.entity.User;
import com.logistic.digitale_logistic.enums.Role;
import com.logistic.digitale_logistic.mapper.UserMapper;
import com.logistic.digitale_logistic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ======================================================
    // TEST LOGIN SUCCESS
    // ======================================================
    @Test
    void testLogin_Success() throws Exception {
        String email = "test@example.com";
        String rawPassword = "1234";
        String encodedPassword = "encoded-pass";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(Role.CLIENT);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        String result = authService.login(email, rawPassword);

        assertNotNull(result);
        assertTrue(result.contains("Login successful"));
        assertTrue(result.contains("CLIENT"));
    }

    // ======================================================
    // TEST LOGIN FAIL → USER NOT FOUND
    // ======================================================
    @Test
    void testLogin_UserNotFound() {
        when(userRepository.findByEmail("x@test.com")).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class,
                () -> authService.login("x@test.com", "1234"));

        assertEquals("User not found", ex.getMessage());
    }

    // ======================================================
    // TEST LOGIN FAIL → INVALID PASSWORD
    // ======================================================
    @Test
    void testLogin_InvalidPassword() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("encoded");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        Exception ex = assertThrows(Exception.class,
                () -> authService.login("test@test.com", "wrong"));

        assertEquals("Invalid credentials", ex.getMessage());
    }

    // ======================================================
    // TEST REGISTER SUCCESS
    // ======================================================
    @Test
    void testRegister_Success() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@test.com");
        req.setPassword("1234");

        User mappedUser = new User();
        mappedUser.setEmail("new@test.com");

        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(userMapper.fromRegisterRequest(req)).thenReturn(mappedUser);
        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = authService.register(req);

        assertNotNull(result);
        assertEquals("encoded1234", result.getPassword());
        assertTrue(result.getActive());
        assertEquals(Role.CLIENT, result.getRole());
    }

    // ======================================================
    // TEST REGISTER FAIL → EMAIL EXISTS
    // ======================================================
    @Test
    void testRegister_EmailAlreadyUsed() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("existing@test.com");

        when(userRepository.findByEmail("existing@test.com"))
                .thenReturn(Optional.of(new User()));

        Exception ex = assertThrows(Exception.class,
                () -> authService.register(req));

        assertEquals("Email already in use", ex.getMessage());
    }
}
