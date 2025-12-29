package com.logistic.digitale_logistic.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 500)
    private String token;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Instant expiresAt;

    private boolean revoked = false;

    private Instant createdAt = Instant.now();
}
