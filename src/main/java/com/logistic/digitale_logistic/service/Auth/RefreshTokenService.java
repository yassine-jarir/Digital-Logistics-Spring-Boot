    package com.logistic.digitale_logistic.service.Auth;

    import com.logistic.digitale_logistic.entity.RefreshToken;
    import com.logistic.digitale_logistic.entity.User;
    import com.logistic.digitale_logistic.repository.RefreshTokenRespository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    import java.time.Instant;

    @Service
    @RequiredArgsConstructor
    public class RefreshTokenService {

        private final RefreshTokenRespository refreshTokenRepository;

        public RefreshToken create(User user, String token) {
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setToken(token);
            refreshToken.setExpiresAt(Instant.now().plusSeconds(7 * 24 * 60 * 60));
            refreshToken.setRevoked(false);

            return refreshTokenRepository.save(refreshToken);
        }

        public RefreshToken validate(String token) {
            RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Refresh token not found"));

            if (refreshToken.isRevoked()) {
                throw new RuntimeException("Refresh token revoked");
            }

            if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
                throw new RuntimeException("Refresh token expired");
            }

            return refreshToken;
        }

        public void revoke(RefreshToken token) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        }
    }
