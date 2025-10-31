package com.logistic.digitale_logistic.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shipment_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shipment_id", nullable = false)
    @ToString.Exclude
    private Shipment shipment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sales_order_line_id", nullable = false)
    @ToString.Exclude
    private SoLine salesOrderLine;

    @Column(name = "quantity_shipped", nullable = false)
    private Integer quantityShipped;
}
