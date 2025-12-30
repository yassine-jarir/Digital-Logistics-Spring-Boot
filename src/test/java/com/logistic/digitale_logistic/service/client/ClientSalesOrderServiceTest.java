//package com.logistic.digitale_logistic.service.client;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.math.BigDecimal;
//import java.util.Collections;
//import java.util.Optional;
//
//import com.logistic.digitale_logistic.dto.ReservationResultDTO;
//import com.logistic.digitale_logistic.dto.SalesOrderDTO;
//import com.logistic.digitale_logistic.dto.SalesOrderWithReservationDTO;
//import com.logistic.digitale_logistic.dto.SoLineDTO;
//import com.logistic.digitale_logistic.entity.Client;
//import com.logistic.digitale_logistic.entity.Product;
//import com.logistic.digitale_logistic.entity.SalesOrder;
//import com.logistic.digitale_logistic.entity.Warehouse;
//import com.logistic.digitale_logistic.mapper.SalesOrderMapper;
//import com.logistic.digitale_logistic.repository.ClientRepository;
//import com.logistic.digitale_logistic.repository.ProductRepository;
//import com.logistic.digitale_logistic.repository.SalesOrderRepository;
//import com.logistic.digitale_logistic.repository.WareHouseRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//public class ClientSalesOrderServiceTest {
//
//    @Mock
//    private ClientRepository clientRepository;
//
//    @Mock
//    private WareHouseRepository warehouseRepository;
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private SalesOrderRepository salesOrderRepository;
//
//    @Mock
//    private InventoryReservationService inventoryReservationService;
//
//    @Mock
//    private SalesOrderMapper salesOrderMapper;
//
//    @InjectMocks
//    private SalesOrderService salesOrderService;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testCreateSalesOrder_Success() {
//        // Input DTO
//        SalesOrderDTO dto = new SalesOrderDTO();
//        dto.setClientId(1L);
//        dto.setWarehouseId(1L);
//
//        SoLineDTO lineDto = new SoLineDTO();
//        lineDto.setProductId(100L);
//        lineDto.setQuantity(5);
//        dto.setLines(Collections.singletonList(lineDto));
//
//        // Mock client, warehouse, product
//        Client client = new com.logistic.digitale_logistic.entity.Client();
//        client.setUserId(1L);
//
//        Warehouse warehouse = new Warehouse();
//        warehouse.setId(1L);
//
//        Product product = new Product();
//        product.setId(100L);
//        product.setActive(true);
//        product.setSellingPrice(BigDecimal.valueOf(20));
//
//        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
//        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
//        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
//
//        // Mock save for SalesOrder
//        SalesOrder savedOrder = new SalesOrder();
//        savedOrder.setId(1L);
//        savedOrder.setOrderNumber("SO-001");
//        savedOrder.setLines(Collections.emptyList());
//        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(savedOrder);
//
//        // Mock reservation service
//        ReservationResultDTO reservationResult = ReservationResultDTO.builder()
//                .salesOrderId(1L)
//                .salesOrderNumber("SO-001")
//                .status("RESERVED")
//                .fullyReserved(true)
//                .hasBackorders(false)
//                .build();
//
//        when(inventoryReservationService.processOrderReservation(1L)).thenReturn(reservationResult);
//
//        // Mock mapper
//        SalesOrderDTO mappedDTO = new SalesOrderDTO();
//        mappedDTO.setId(1L);
//        mappedDTO.setOrderNumber("SO-001");
//        when(salesOrderMapper.toDTO(savedOrder)).thenReturn(mappedDTO);
//
//        // Call the method
//        SalesOrderWithReservationDTO result = salesOrderService.createSalesOrder(dto);
//
//        // Verify
//        assertNotNull(result);
//        assertNotNull(result.getSalesOrder());
//        assertEquals(1L, result.getSalesOrder().getId());
//        assertNotNull(result.getReservationResult());
//        assertTrue(result.getReservationResult().isFullyReserved());
//
//        verify(clientRepository).findById(1L);
//        verify(warehouseRepository).findById(1L);
//        verify(productRepository).findById(100L);
//        verify(salesOrderRepository).save(any(SalesOrder.class));
//        verify(inventoryReservationService).processOrderReservation(1L);
//    }
//}
