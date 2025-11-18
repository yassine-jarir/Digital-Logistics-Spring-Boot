package com.logistic.digitale_logistic.controller.Client;

import com.logistic.digitale_logistic.dto.ShipmentDTO;
import com.logistic.digitale_logistic.service.client.ShipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShipmentControllerTest {

    @Mock
    private ShipmentService shipmentService;

    @InjectMocks
    private ShipmentController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ===================== TEST 1: createShipment() =====================
    @Test
    void testCreateShipment() {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setId(1L);

        when(shipmentService.createShipment(1L)).thenReturn(dto);

        ShipmentDTO result = controller.createShipment(1L);

        assertEquals(1L, result.getId());
        verify(shipmentService, times(1)).createShipment(1L);
    }

    // ===================== TEST 2: shipShipment() =====================
    @Test
    void testShipShipment() {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setId(10L);

        ShipmentController.ShipRequest request =
                new ShipmentController.ShipRequest("TRACK123", "DHL");

        when(shipmentService.shipShipment(10L, "TRACK123", "DHL"))
                .thenReturn(dto);

        ShipmentDTO result = controller.shipShipment(10L, request);

        assertEquals(10L, result.getId());
        verify(shipmentService, times(1))
                .shipShipment(10L, "TRACK123", "DHL");
    }

    // ===================== TEST 3: markAsDelivered() =====================
    @Test
    void testMarkAsDelivered() {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setId(99L);

        when(shipmentService.markAsDelivered(99L)).thenReturn(dto);

        ShipmentDTO result = controller.markAsDelivered(99L);

        assertEquals(99L, result.getId());
        verify(shipmentService, times(1)).markAsDelivered(99L);
    }
}
