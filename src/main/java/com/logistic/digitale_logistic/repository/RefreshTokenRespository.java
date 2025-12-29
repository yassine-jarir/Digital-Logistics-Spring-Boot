package com.logistic.digitale_logistic.repository;

import com.logistic.digitale_logistic.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRespository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}
