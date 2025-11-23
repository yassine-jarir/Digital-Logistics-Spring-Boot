package com.logistic.digitale_logistic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration for Digital Logistics API
 *
 * This configuration implements Basic Authentication with role-based access control.
 *
 * Available Roles:
 * - ADMIN: Full access to all endpoints
 * - WAREHOUSE_MANAGER: Access to inventory and shipment management
 * - CLIENT: Access to orders and client-specific operations
 *
 * Public Endpoints:
 * - Root, error pages, and static resources
 * - Swagger UI and API documentation
 * - Authentication endpoints (login/register)
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Configure security filter chain with Basic Auth and role-based access control
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers("/", "/error", "/favicon.ico").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // Swagger UI and OpenAPI documentation - public access
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // Static resources - public access
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        // Admin-only endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/products/**").hasRole("ADMIN")

                        // Admin and Warehouse Manager endpoints
                        .requestMatchers("/api/inventory/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers("/api/shipments/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")

                        // Client and Admin endpoints
                        .requestMatchers("/api/orders/**").hasAnyRole("CLIENT", "ADMIN")

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * Configure in-memory user details with predefined users for each role
     *
     * Default Users:
     * - admin/admin123 (ADMIN role)
     * - manager/manager123 (WAREHOUSE_MANAGER role)
     * - client/client123 (CLIENT role)
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails warehouseManager = User.builder()
                .username("manager")
                .password(passwordEncoder().encode("manager123"))
                .roles("WAREHOUSE_MANAGER")
                .build();

        UserDetails client = User.builder()
                .username("client")
                .password(passwordEncoder().encode("client123"))
                .roles("CLIENT")
                .build();

        return new InMemoryUserDetailsManager(admin, warehouseManager, client);
    }

    /**
     * Configure password encoder using BCrypt hashing algorithm
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
