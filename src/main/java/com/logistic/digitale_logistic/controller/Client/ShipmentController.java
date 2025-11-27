package com.logistic.digitale_logistic.controller.Client;

import com.logistic.digitale_logistic.dto.ShipmentDTO;
import com.logistic.digitale_logistic.service.client.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Client - Shipments", description = "Client endpoints for managing shipments and tracking deliveries")
@SecurityRequirement(name = "Bearer Authentication")
public class ShipmentController {

    private final ShipmentService shipmentService;

    /**
     * Create a shipment for a reserved sales order
     *
     * @param salesOrderId the sales order ID
     * @return created shipment
     */
    @Operation(
            summary = "Create shipment",
            description = "Create a new shipment for a reserved sales order. The order must have inventory reserved before shipping"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Shipment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid sales order or order not reserved"),
            @ApiResponse(responseCode = "404", description = "Sales order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Client role required")
    })
    @PostMapping("/create/{salesOrderId}")
    @PreAuthorize("hasRole('CLIENT')")
    @ResponseStatus(HttpStatus.CREATED)
    public ShipmentDTO createShipment(
            @Parameter(description = "Sales Order ID", required = true, example = "1")
            @PathVariable Long salesOrderId) {
        return shipmentService.createShipment(salesOrderId);
    }

    /**
     * Ship a planned shipment
     *
     * @param shipmentId the shipment ID
     * @param request shipping details (tracking number, carrier)
     * @return updated shipment
     */
    @Operation(
            summary = "Ship shipment",
            description = "Mark a shipment as shipped and provide tracking information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipment shipped successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid shipment status"),
            @ApiResponse(responseCode = "404", description = "Shipment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/{shipmentId}/ship")
    @ResponseStatus(HttpStatus.OK)
    public ShipmentDTO shipShipment(
            @Parameter(description = "Shipment ID", required = true, example = "1")
            @PathVariable Long shipmentId,
            @RequestBody ShipRequest request) {
        return shipmentService.shipShipment(shipmentId, request.getTrackingNumber(), request.getCarrier());
    }

    /**
     * Mark a shipment as delivered
     *
     * @param shipmentId the shipment ID
     * @return updated shipment
     */
    @Operation(
            summary = "Mark shipment as delivered",
            description = "Mark a shipped shipment as delivered to the customer"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipment marked as delivered"),
            @ApiResponse(responseCode = "400", description = "Invalid shipment status"),
            @ApiResponse(responseCode = "404", description = "Shipment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/{shipmentId}/deliver")
    @ResponseStatus(HttpStatus.OK)
    public ShipmentDTO markAsDelivered(
            @Parameter(description = "Shipment ID", required = true, example = "1")
            @PathVariable Long shipmentId) {
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
