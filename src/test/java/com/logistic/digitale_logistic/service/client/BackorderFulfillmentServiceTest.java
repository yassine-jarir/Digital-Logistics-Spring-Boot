package com.logistic.digitale_logistic.service.client;

import com.logistic.digitale_logistic.entity.*;
import com.logistic.digitale_logistic.enums.BackorderStatus;
import com.logistic.digitale_logistic.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BackorderFulfillmentServiceTest {

    @Mock
    private BackorderRepository backorderRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @Mock
    private SoLineRepository soLineRepository;

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @InjectMocks
    private BackorderFulfillmentService service;

    private Backorder backorder;
    private Product product;
    private Warehouse warehouse;
    private SoLine soLine;
    private SalesOrder salesOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setId(1L);

        warehouse = new Warehouse();
        warehouse.setId(1L);

        salesOrder = new SalesOrder();
        salesOrder.setOrderNumber("SO-001");
        salesOrder.setStatus("CREATED");

        soLine = new SoLine();
        soLine.setId(1L);
        soLine.setSalesOrder(salesOrder);
        soLine.setReservedQuantity(0);

        backorder = new Backorder();
        backorder.setId(1L);
        backorder.setProduct(product);
        backorder.setWarehouse(warehouse);
        backorder.setSoLine(soLine);
        backorder.setQuantityBackordered(10);
        backorder.setQuantityFulfilled(0);
        backorder.setStatus(BackorderStatus.PENDING);
    }

    @Test
    void testProcessPendingBackorders_FulfillPartial() {
        when(backorderRepository.findPendingBackordersByProductAndWarehouse(1L, 1L))
                .thenReturn(List.of(backorder));

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        inventory.setQtyOnHand(0);
        inventory.setQtyReserved(0);

        when(inventoryRepository.findByProduct_IdAndWarehouse_Id(1L, 1L))
                .thenReturn(Optional.of(inventory));

        service.processPendingBackorders(1L, 1L, 5, null);

        assertEquals(5, backorder.getQuantityFulfilled());
        assertEquals(5, inventory.getQtyReserved());
        assertEquals(5, soLine.getReservedQuantity());

        verify(backorderRepository, times(1)).save(backorder);
        verify(inventoryRepository, times(1)).save(inventory);
        verify(soLineRepository, times(1)).save(soLine);
    }

    @Test
    void testProcessPendingBackorders_FulfillAll() {
        when(backorderRepository.findPendingBackordersByProductAndWarehouse(1L, 1L))
                .thenReturn(List.of(backorder));

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        inventory.setQtyOnHand(0);
        inventory.setQtyReserved(0);

        when(inventoryRepository.findByProduct_IdAndWarehouse_Id(1L, 1L))
                .thenReturn(Optional.of(inventory));

        service.processPendingBackorders(1L, 1L, 10, null);

        assertEquals(10, backorder.getQuantityFulfilled());
        assertEquals(10, inventory.getQtyReserved());
        assertEquals(10, soLine.getReservedQuantity());
        assertEquals(BackorderStatus.FULFILLED, backorder.getStatus());
    }

    @Test
    void testProcessPendingBackorders_NoPendingBackorders() {
        when(backorderRepository.findPendingBackordersByProductAndWarehouse(1L, 1L))
                .thenReturn(List.of());

        service.processPendingBackorders(1L, 1L, 10, null);

        // No backorders, so nothing should be saved
        verify(backorderRepository, never()).save(any());
    }

    @Test
    void testGetBackordersForSalesOrder() {
        when(backorderRepository.findBySalesOrderId(1L)).thenReturn(List.of(backorder));

        List<Backorder> result = service.getBackordersForSalesOrder(1L);

        assertEquals(1, result.size());
        assertEquals(backorder, result.get(0));
    }
}
