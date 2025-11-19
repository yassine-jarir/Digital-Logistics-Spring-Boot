package com.logistic.digitale_logistic.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Digital Logistics & Supply Chain Platform");
        response.put("version", "0.0.1-SNAPSHOT");
        response.put("status", "running");
        response.put("documentation", "/swagger-ui.html");
        response.put("endpoints", Map.of(
                "auth", "/api/auth/**",
                "admin", "/api/admin/**",
                "client", "/api/client/**",
                "warehouse", "/api/warehouse-manager/**"
        ));
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return response;
    }
}

