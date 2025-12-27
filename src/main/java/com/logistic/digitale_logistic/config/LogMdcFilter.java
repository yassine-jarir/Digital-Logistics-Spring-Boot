package com.logistic.digitale_logistic.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LogMdcFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            MDC.put("endpoint", request.getMethod() + " " + request.getRequestURI());

            filterChain.doFilter(request, response);

            MDC.put("status", String.valueOf(response.getStatus()));

        } finally {
            MDC.clear();
        }
    }
}
