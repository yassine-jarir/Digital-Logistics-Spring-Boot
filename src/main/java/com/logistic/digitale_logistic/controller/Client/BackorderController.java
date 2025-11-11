package com.logistic.digitale_logistic.controller.Client;

import com.logistic.digitale_logistic.dto.BackorderDTO;
import com.logistic.digitale_logistic.entity.Backorder;
import com.logistic.digitale_logistic.mapper.BackorderMapper;
import com.logistic.digitale_logistic.service.client.BackorderFulfillmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/client/backorders")
@RequiredArgsConstructor
public class BackorderController {

    private final BackorderFulfillmentService backorderFulfillmentService;
    private final BackorderMapper backorderMapper;

    /**
     * Get all backorders for a sales order
     *
     * @param salesOrderId the sales order ID
     * @return list of backorders
     */
    @GetMapping("/sales-order/{salesOrderId}")
    @PreAuthorize("hasRole('CLIENT')")
    @ResponseStatus(HttpStatus.OK)
    public List<BackorderDTO> getBackordersForSalesOrder(@PathVariable Long salesOrderId) {
        List<Backorder> backorders = backorderFulfillmentService.getBackordersForSalesOrder(salesOrderId);
        return backorders.stream()
                .map(backorderMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cancel a backorder
     *
     * @param backorderId the backorder ID to cancel
     */
    @DeleteMapping("/{backorderId}")
    @PreAuthorize("hasRole('CLIENT')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelBackorder(@PathVariable Long backorderId) {
        backorderFulfillmentService.cancelBackorder(backorderId);
    }
}

