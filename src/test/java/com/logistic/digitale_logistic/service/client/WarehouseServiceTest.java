//package com.logistic.digitale_logistic.service.client;
//
//import com.logistic.digitale_logistic.dto.ProductInventoryDTO;
//import com.logistic.digitale_logistic.dto.WarehouseListDTO;
//import com.logistic.digitale_logistic.entity.Inventory;
//import com.logistic.digitale_logistic.entity.Product;
//import com.logistic.digitale_logistic.entity.Warehouse;
//import com.logistic.digitale_logistic.repository.InventoryRepository;
//import com.logistic.digitale_logistic.repository.WareHouseRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class WarehouseServiceTest {
//
//    @Mock
//    private WareHouseRepository warehouseRepository;
//
//    @Mock
//    private InventoryRepository inventoryRepository;
//
//    @InjectMocks
//    private WarehouseViewService warehouseViewService;
//
//    private Warehouse warehouse;
//    private Product product;
//    private Inventory inventory;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        warehouse = new Warehouse();
//        warehouse.setId(1L);
//        warehouse.setName("Main Warehouse");
//        warehouse.setLocation("Casablanca");
//        warehouse.setActive(true);
//
//        product = new Product();
//        product.setId(1L);
//        product.setSku("PROD-001");
//        product.setName("Product 1");
//        product.setActive(true);
//        product.setSellingPrice(BigDecimal.valueOf(100.0));
//
//        inventory = new Inventory();
//        inventory.setProduct(product);
//        inventory.setQtyOnHand(50);
//        inventory.setQtyReserved(10);
//        inventory.setQtyAvailable(40); // DB-calculated field
//        inventory.setWarehouse(warehouse);
//    }
//
//    @Test
//    void testGetAllWarehousesWithInventory() {
//        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));
//        when(inventoryRepository.findByWarehouse(warehouse)).thenReturn(List.of(inventory));
//
//        List<WarehouseListDTO> result = warehouseViewService.getAllWarehousesWithInventory();
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        WarehouseListDTO dto = result.get(0);
//        assertEquals("Main Warehouse", dto.getName());
//        assertEquals(1, dto.getProducts().size());
//        ProductInventoryDTO prodDTO = dto.getProducts().get(0);
//        assertEquals("PROD-001", prodDTO.getSku());
//
//        verify(warehouseRepository, times(1)).findAll();
//        verify(inventoryRepository, times(1)).findByWarehouse(warehouse);
//    }
//
//    @Test
//    void testGetWarehouseWithInventory_Success() {
//        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
//        when(inventoryRepository.findByWarehouse(warehouse)).thenReturn(List.of(inventory));
//
//        WarehouseListDTO dto = warehouseViewService.getWarehouseWithInventory(1L);
//
//        assertNotNull(dto);
//        assertEquals("Main Warehouse", dto.getName());
//        assertEquals(1, dto.getProducts().size());
//    }
//
//    @Test
//    void testGetWarehouseWithInventory_NotFound() {
//        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());
//
//        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
//                () -> warehouseViewService.getWarehouseWithInventory(1L));
//
//        assertTrue(ex.getMessage().contains("Warehouse not found"));
//    }
//
//    @Test
//    void testGetWarehouseWithInventory_NotActive() {
//        warehouse.setActive(false);
//        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
//
//        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
//                () -> warehouseViewService.getWarehouseWithInventory(1L));
//
//        assertTrue(ex.getMessage().contains("Warehouse is not active"));
//    }
//}
