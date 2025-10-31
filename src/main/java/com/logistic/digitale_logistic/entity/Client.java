package com.logistic.digitale_logistic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @Column(length = 50)
    private String phone;

    @Column(columnDefinition = "text")
    private String address;

    @Column(name = "credit_limit", precision = 10, scale = 2)
    private BigDecimal creditLimit = BigDecimal.ZERO;
}
