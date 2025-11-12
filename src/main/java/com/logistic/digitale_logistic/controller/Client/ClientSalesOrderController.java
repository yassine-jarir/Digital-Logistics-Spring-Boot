package com.logistic.digitale_logistic.controller.Client;
import com.logistic.digitale_logistic.dto.SalesOrderDTO;
import com.logistic.digitale_logistic.dto.SalesOrderWithReservationDTO;
import com.logistic.digitale_logistic.service.client.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client/sales-orders")
@RequiredArgsConstructor
public class ClientSalesOrderController {

    private final SalesOrderService salesOrderService;

    /**
     * Create a new sales order with automatic reservation
     *
     * @param dto the sales order details
     * @return the created sales order with reservation result
     */
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    @ResponseStatus(HttpStatus.CREATED)
    public SalesOrderWithReservationDTO createSalesOrder(@Valid @RequestBody SalesOrderDTO dto) {
        return salesOrderService.createSalesOrder(dto);
    }

    /**
     * Get all sales orders for the authenticated client
     *
     * @param authentication the authenticated user
     * @return list of sales orders
     */
    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    @ResponseStatus(HttpStatus.OK)
    public List<SalesOrderDTO> getMyOrders(Authentication authentication) {
        // Extract client ID from authenticated user
        Long clientId = Long.parseLong(authentication.getName());
        return salesOrderService.getClientOrders(clientId);
    }

}
