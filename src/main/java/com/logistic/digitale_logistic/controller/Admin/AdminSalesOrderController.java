package com.logistic.digitale_logistic.controller.Admin;

import com.logistic.digitale_logistic.dto.SalesOrderDTO;
import com.logistic.digitale_logistic.service.client.SalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sales-orders")
@RequiredArgsConstructor
public class AdminSalesOrderController {

    private final SalesOrderService salesOrderService;

    /**
     * Get all sales orders (admin can view all orders)
     *
     * @return list of all sales orders
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public List<SalesOrderDTO> getAllSalesOrders() {
        return salesOrderService.getAllSalesOrders();
    }

    /**
     * Get a specific sales order by ID
     *
     * @param id the sales order ID
     * @return the sales order details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public SalesOrderDTO getSalesOrderById(@PathVariable Long id) {
        return salesOrderService.getSalesOrderById(id);
    }
}

