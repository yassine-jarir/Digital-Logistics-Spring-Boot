package com.logistic.digitale_logistic.service.client;

import com.logistic.digitale_logistic.dto.ShipmentDTO;
import com.logistic.digitale_logistic.entity.*;
import com.logistic.digitale_logistic.mapper.ShipmentMapper;
import com.logistic.digitale_logistic.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;
    @Mock
    private SalesOrderRepository salesOrderRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryMovementRepository inventoryMovementRepository;
    @Mock
    private ShipmentMapper shipmentMapper;

    @InjectMocks
    private ShipmentService shipmentService;

    private SalesOrder salesOrder;
    private ShipmentLine shipmentLine;
    private SoLine soLine;
    private Product product;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Main Warehouse");
        warehouse.setLocation("Location A");

        product = new Product();
        product.setId(1L);
        product.setSku("SKU-001");
        product.setName("Product A");

        soLine = new SoLine();
        soLine.setId(1L);
        soLine.setProduct(product);
        soLine.setOrderedQuantity(5);
        soLine.setReservedQuantity(5);
        soLine.setUnitPrice(BigDecimal.valueOf(10.00));

        salesOrder = new SalesOrder();
        salesOrder.setId(1L);
        salesOrder.setStatus("RESERVED");
        salesOrder.setWarehouse(warehouse);

        // Initialize the lines list properly
        List<SoLine> soLinesList = new ArrayList<>();
        soLinesList.add(soLine);
        salesOrder.setLines(soLinesList);

        // Set the sales order reference in soLine
        soLine.setSalesOrder(salesOrder);

        shipmentLine = new ShipmentLine();
        shipmentLine.setId(1L);
        shipmentLine.setProduct(product);
        shipmentLine.setQuantity(5);
        shipmentLine.setSalesOrderLine(soLine);

        // Initialize shipment reference to avoid null in lists
        Shipment dummyShipment = new Shipment();
        dummyShipment.setId(1L);
        shipmentLine.setShipment(dummyShipment);
    }

    @Test
    void testCreateShipment_Success() {
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));

        Shipment shipment = new Shipment();
        shipment.setLines(new ArrayList<>());
        when(shipmentRepository.save(any())).thenReturn(shipment);
        when(shipmentMapper.toDTO(any())).thenReturn(new ShipmentDTO());

        ShipmentDTO dto = shipmentService.createShipment(1L);

        assertNotNull(dto);
        verify(shipmentRepository, times(1)).save(any());
    }

    @Test
    void testCreateShipment_OrderNotReserved() {
        salesOrder.setStatus("CREATED");
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> shipmentService.createShipment(1L));

        assertTrue(exception.getMessage().contains("Order must be RESERVED"));
    }

    @Test
    void testShipShipment_Success() {
        Shipment shipment = new Shipment();
        shipment.setId(1L);
        shipment.setStatus("PLANNED");
        shipment.setSalesOrder(salesOrder);
        shipment.setLines(List.of(shipmentLine));

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        inventory.setQtyOnHand(10);
        inventory.setQtyReserved(5);

        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(inventoryRepository.findByProduct_IdAndWarehouse_Id(product.getId(), warehouse.getId()))
                .thenReturn(Optional.of(inventory));
        when(shipmentRepository.save(any())).thenReturn(shipment);
        when(salesOrderRepository.save(any())).thenReturn(salesOrder);
        when(shipmentMapper.toDTO(any())).thenReturn(new ShipmentDTO());

        ShipmentDTO dto = shipmentService.shipShipment(1L, "TRACK123", "DHL");

        assertNotNull(dto);
        assertEquals(5, shipmentLine.getQuantity()); // quantityShipped same as reserved
        verify(inventoryRepository, times(1)).save(inventory);
        verify(shipmentRepository, times(1)).save(shipment);
        verify(salesOrderRepository, times(1)).save(salesOrder);
    }

    @Test
    void testMarkAsDelivered_Success() {
        Shipment shipment = new Shipment();
        shipment.setId(1L);
        shipment.setStatus("SHIPPED");
        shipment.setSalesOrder(salesOrder);

        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any())).thenReturn(shipment);
        when(salesOrderRepository.save(any())).thenReturn(salesOrder);
        when(shipmentMapper.toDTO(any())).thenReturn(new ShipmentDTO());

        ShipmentDTO dto = shipmentService.markAsDelivered(1L);

        assertNotNull(dto);
        assertEquals("DELIVERED", shipment.getStatus());
        verify(salesOrderRepository, times(1)).save(salesOrder);
    }
}
