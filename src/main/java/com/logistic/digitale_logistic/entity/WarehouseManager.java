package com.logistic.digitale_logistic.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "warehouse_managers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseManager {

    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    @ToString.Exclude
    private Warehouse warehouse;
}
