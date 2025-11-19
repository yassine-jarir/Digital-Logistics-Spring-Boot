package com.logistic.digitale_logistic.service.client;

import com.logistic.digitale_logistic.dto.ReservationResultDTO;
import com.logistic.digitale_logistic.dto.SalesOrderDTO;
import com.logistic.digitale_logistic.dto.SalesOrderWithReservationDTO;
import com.logistic.digitale_logistic.dto.SoLineDTO;
import com.logistic.digitale_logistic.entity.*;
import com.logistic.digitale_logistic.mapper.SalesOrderMapper;
import com.logistic.digitale_logistic.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SalesOrderServiceTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private WareHouseRepository warehouseRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SalesOrderMapper salesOrderMapper;

    @Mock
    private InventoryReservationService inventoryReservationService;

    @InjectMocks
    private SalesOrderService salesOrderService;

    @Test
    void testCreateSalesOrder_SuccessWithReservation() {

        // ------------------------------
        // 1) MOCK DTO INPUT
        // ------------------------------
        SalesOrderDTO dto = new SalesOrderDTO();
        dto.setClientId(1L);
        dto.setWarehouseId(1L);

        SoLineDTO lineDTO = new SoLineDTO();
        lineDTO.setProductId(10L);
        lineDTO.setQuantity(2);
        dto.setLines(List.of(lineDTO));

        // ------------------------------
        // 2) MOCK REPOSITORY ENTITIES
        // ------------------------------
        Client client = new Client();
        client.setUserId(1L);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);

        Product product = new Product();
        product.setId(10L);
        product.setActive(true);
        product.setSellingPrice(new BigDecimal("100"));

        // SalesOrder saved by repo
        SalesOrder savedOrder = new SalesOrder();
        savedOrder.setId(50L);
        savedOrder.setOrderNumber("SO-123456");
        savedOrder.setClient(client);
        savedOrder.setWarehouse(warehouse);
        savedOrder.setStatus("CREATED");

        // ------------------------------
        // 3) MOCK BEHAVIOR
        // ------------------------------
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        when(salesOrderRepository.save(any(SalesOrder.class)))
                .thenReturn(savedOrder);

        when(salesOrderRepository.findById(50L))
                .thenReturn(Optional.of(savedOrder));

        // Mock reservation result
        ReservationResultDTO reservationResult = ReservationResultDTO.builder()
                .salesOrderId(50L)
                .status("RESERVED")
                .fullyReserved(true)
                .message("OK")
                .build();

        when(inventoryReservationService.processOrderReservation(50L))
                .thenReturn(reservationResult);

        // Mapper output (for final response)
        SalesOrderDTO mappedDTO = new SalesOrderDTO();
        mappedDTO.setId(50L);
        mappedDTO.setClientId(1L);
        mappedDTO.setWarehouseId(1L);
        when(salesOrderMapper.toDTO(savedOrder)).thenReturn(mappedDTO);

        // ------------------------------
        // 4) RUN SERVICE METHOD
        // ------------------------------
        SalesOrderWithReservationDTO result = salesOrderService.createSalesOrder(dto);

        // ------------------------------
        // 5) ASSERT LOGIC
        // ------------------------------

        assertNotNull(result);
        assertNotNull(result.getSalesOrder());
        assertNotNull(result.getReservationResult());

        assertEquals(50L, result.getReservationResult().getSalesOrderId());
        assertEquals("RESERVED", result.getReservationResult().getStatus());
        assertEquals(true, result.getReservationResult().isFullyReserved());

        // Ensure mapper was used
        assertEquals(50L, result.getSalesOrder().getId());

        // Ensure reservation service was called
        assertEquals("OK", result.getReservationResult().getMessage());
    }
}
