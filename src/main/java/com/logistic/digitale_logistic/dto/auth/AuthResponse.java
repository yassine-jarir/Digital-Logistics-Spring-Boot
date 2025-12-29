package com.logistic.digitale_logistic.dto.auth;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
}
