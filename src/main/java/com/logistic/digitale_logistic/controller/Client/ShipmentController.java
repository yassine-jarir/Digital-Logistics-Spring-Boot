package com.logistic.digitale_logistic.controller.Client;

import com.logistic.digitale_logistic.dto.ShipmentDTO;
import com.logistic.digitale_logistic.service.client.ShipmentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    /**
     * Create a shipment for a reserved sales order
     *
     * @param salesOrderId the sales order ID
     * @return created shipment
     */
    @PostMapping("/create/{salesOrderId}")
    @PreAuthorize("hasRole('CLIENT')")
    @ResponseStatus(HttpStatus.CREATED)
    public ShipmentDTO createShipment(@PathVariable Long salesOrderId) {
        return shipmentService.createShipment(salesOrderId);
    }

    /**
     * Ship a planned shipment
     *
     * @param shipmentId the shipment ID
     * @param request shipping details (tracking number, carrier)
     * @return updated shipment
     */
    @PostMapping("/{shipmentId}/ship")
    @ResponseStatus(HttpStatus.OK)
    public ShipmentDTO shipShipment(@PathVariable Long shipmentId, @RequestBody ShipRequest request) {
        return shipmentService.shipShipment(shipmentId, request.getTrackingNumber(), request.getCarrier());
    }

    /**
     * Mark a shipment as delivered
     *
     * @param shipmentId the shipment ID
     * @return updated shipment
     */
    @PostMapping("/{shipmentId}/deliver")
    @ResponseStatus(HttpStatus.OK)
    public ShipmentDTO markAsDelivered(@PathVariable Long shipmentId) {
        return shipmentService.markAsDelivered(shipmentId);
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShipRequest {
        private String trackingNumber;
        private String carrier;
    }
}
