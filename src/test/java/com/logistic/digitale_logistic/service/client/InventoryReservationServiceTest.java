//package com.logistic.digitale_logistic.service.client;
//
//import com.logistic.digitale_logistic.dto.ReservationResultDTO;
//import com.logistic.digitale_logistic.entity.*;
//import com.logistic.digitale_logistic.repository.BackorderRepository;
//import com.logistic.digitale_logistic.repository.InventoryRepository;
//import com.logistic.digitale_logistic.repository.SalesOrderRepository;
//import com.logistic.digitale_logistic.repository.SoLineRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class InventoryReservationServiceTest {
//
//    @Mock
//    private InventoryRepository inventoryRepository;
//
//    @Mock
//    private BackorderRepository backorderRepository;
//
//    @Mock
//    private SalesOrderRepository salesOrderRepository;
//
//    @Mock
//    private SoLineRepository soLineRepository;
//
//    @InjectMocks
//    private InventoryReservationService inventoryReservationService;
//
//    @Test
//    void testProcessOrderReservation_FullReservation() {
//
//        // ---- PREPARE MOCK DATA ----
//
//        // Warehouse
//        Warehouse warehouse = new Warehouse();
//        warehouse.setId(1L);
//        warehouse.setName("Main WH");
//
//        // Product
//        Product product = new Product();
//        product.setId(1L);
//        product.setSku("SKU-123");
//        product.setName("Laptop");
//
//        // SO Line
//        SoLine soLine = new SoLine();
//        soLine.setId(10L);
//        soLine.setProduct(product);
//        soLine.setOrderedQuantity(5);
//        soLine.setReservedQuantity(0);
//
//        // SalesOrder
//
//        SalesOrder salesOrder = new SalesOrder();
//        salesOrder.setId(100L);
//        salesOrder.setOrderNumber("SO-100");
//        salesOrder.setStatus("CREATED");
//        salesOrder.setWarehouse(warehouse);
//        salesOrder.setLines(List.of(soLine));
//
//        // Inventory (available stock 10 units)
//        Inventory inventory = new Inventory();
//        inventory.setId(50L);
//        inventory.setProduct(product);
//        inventory.setWarehouse(warehouse);
//        inventory.setQtyAvailable(10);
//        inventory.setQtyReserved(0);
//
//        // ---- MOCKING BEHAVIOR ----
//
//        when(salesOrderRepository.findById(100L))
//                .thenReturn(Optional.of(salesOrder));
//
//        when(inventoryRepository.findByProduct_IdAndWarehouse_Id(1L, 1L))
//                .thenReturn(Optional.of(inventory));
//
//        when(inventoryRepository.save(any(Inventory.class)))
//                .thenAnswer(i -> i.getArgument(0));
//
//        when(soLineRepository.save(any(SoLine.class)))
//                .thenAnswer(i -> i.getArgument(0));
//
//        when(salesOrderRepository.save(any(SalesOrder.class)))
//                .thenAnswer(i -> i.getArgument(0));
//
//        // ---- RUN SERVICE ----
//        ReservationResultDTO result = inventoryReservationService.processOrderReservation(100L);
//
//        // ---- ASSERTIONS ----
//        assertEquals("RESERVED", result.getStatus());
//        assertTrue(result.isFullyReserved());
//        assertFalse(result.isHasBackorders());
//        assertEquals(100L, result.getSalesOrderId());
//        assertEquals("SO-100", result.getSalesOrderNumber());
//
//        // Inventory was updated
//        assertEquals(5, inventory.getQtyReserved());
//
//        // SO Line updated
//        assertEquals(5, soLine.getReservedQuantity());
//    }
//}
