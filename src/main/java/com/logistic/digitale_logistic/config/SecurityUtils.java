package com.logistic.digitale_logistic.config;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;


public final class SecurityUtils {

    private SecurityUtils() {}

    public static String currentUserSub() {
        Jwt jwt = (Jwt) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return jwt.getSubject();
    }
}
