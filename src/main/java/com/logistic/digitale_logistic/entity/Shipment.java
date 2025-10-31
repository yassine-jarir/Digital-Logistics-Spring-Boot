package com.logistic.digitale_logistic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shipment_number", nullable = false, length = 100, unique = true)
    private String shipmentNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sales_order_id", nullable = false)
    @ToString.Exclude
    private SalesOrder salesOrder;

    @Column(name = "tracking_number", length = 255)
    private String trackingNumber;

    @Column(length = 255)
    private String carrier;

    @Column(nullable = false, length = 50)
    private String status = "PLANNED";

    @Column(name = "planned_ship_date")
    private LocalDate plannedShipDate;

    @Column(name = "actual_ship_date")
    private LocalDate actualShipDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShipmentLine> lines = new ArrayList<>();
}
