package com.logistic.digitale_logistic.controller.warehouse_manager;

import com.logistic.digitale_logistic.dto.SalesOrderDTO;
import com.logistic.digitale_logistic.service.client.SalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse-manager/sales-orders")
@RequiredArgsConstructor
public class WarehouseManagerSalesOrderController {

    private final SalesOrderService salesOrderService;

    /**
     * Get all sales orders (warehouse manager can view all orders)
     *
     * @return list of all sales orders
     */
    @GetMapping
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    public List<SalesOrderDTO> getAllSalesOrders() {
        return salesOrderService.getAllSalesOrders();
    }


}

