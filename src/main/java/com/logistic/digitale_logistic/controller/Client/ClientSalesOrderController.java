package com.logistic.digitale_logistic.controller.Client;

import com.logistic.digitale_logistic.dto.SalesOrderDTO;
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
     * Create a new sales order
     *
     * @param dto the sales order details
     * @return the created sales order with generated IDs
     */
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    @ResponseStatus(HttpStatus.CREATED)
    public SalesOrderDTO createSalesOrder(@Valid @RequestBody SalesOrderDTO dto) {
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

    /**
     * Get all sales orders (for admin/manager view)
     *
     * @return list of all sales orders
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    public List<SalesOrderDTO> getAllOrders() {
        return salesOrderService.getAllSalesOrders();
    }

    /**
     * Get a specific sales order by ID
     *
     * @param id the sales order ID
     * @return the sales order details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN', 'WAREHOUSE_MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    public SalesOrderDTO getSalesOrderById(@PathVariable Long id) {
        return salesOrderService.getSalesOrderById(id);
    }
}
