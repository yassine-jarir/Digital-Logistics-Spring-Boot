package com.logistic.digitale_logistic.security;

import com.logistic.digitale_logistic.entity.User;
import com.logistic.digitale_logistic.enums.Role;
import com.logistic.digitale_logistic.jwt.JwtService;
import com.logistic.digitale_logistic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserRepository userRepository;

    private User admin;
    private User client;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        admin = new User();
        admin.setEmail("admin@test.com");
        admin.setName("Admin User");
        admin.setPassword("password");
        admin.setRole(Role.ADMIN);
        admin.setActive(true);
        userRepository.save(admin);

        client = new User();
        client.setEmail("client@test.com");
        client.setName("Client User");
        client.setPassword("password");
        client.setRole(Role.CLIENT);
        client.setActive(true);
        userRepository.save(client);
    }

    @Test
    void shouldReturn401_whenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403_whenClientAccessAdminEndpoint() throws Exception {
        String clientToken = jwtService.generateAccessToken(client);

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn200_whenAdminAccessAdminEndpoint() throws Exception {
        String adminToken = jwtService.generateAccessToken(admin);

        mockMvc.perform(get("/api/admin/sales-orders")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
