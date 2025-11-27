package com.logistic.digitale_logistic.controller.Admin;

import com.logistic.digitale_logistic.controller.AdminPurchaseOrderController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.logistic.digitale_logistic.dto.PurchaseOrderDTO;
import com.logistic.digitale_logistic.enums.PurchaseOrderStatus;
import com.logistic.digitale_logistic.service.warehouse_manager.PurchaseOrderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminPurchaseOrderControllerTest {

    @Mock
    private PurchaseOrderService purchaseOrderService;

    @InjectMocks
    private AdminPurchaseOrderController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPurchaseOrders_NoStatus() {
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setId(1L);

        when(purchaseOrderService.getAllPurchaseOrders())
                .thenReturn(List.of(dto));

        List<PurchaseOrderDTO> result = controller.getAllPurchaseOrders(null);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testGetAllPurchaseOrders_WithStatus() {
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setId(5L);

        when(purchaseOrderService.getPurchaseOrdersByStatus(PurchaseOrderStatus.APPROVED))
                .thenReturn(List.of(dto));

        List<PurchaseOrderDTO> result = controller.getAllPurchaseOrders("APPROVED");

        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getId());
    }

    @Test
    void testGetPurchaseOrderById() {
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setId(10L);

        when(purchaseOrderService.getPurchaseOrderById(10L))
                .thenReturn(dto);

        PurchaseOrderDTO result = controller.getPurchaseOrderById(10L);

        assertEquals(10L, result.getId());
    }

    @Test
    void testApprovePurchaseOrder() {
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setId(20L);

        when(purchaseOrderService.approvePurchaseOrder(20L))
                .thenReturn(dto);

        PurchaseOrderDTO result = controller.approvePurchaseOrder(20L);

        assertEquals(20L, result.getId());
    }
}
