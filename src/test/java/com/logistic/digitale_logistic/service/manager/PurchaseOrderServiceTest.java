//package com.logistic.digitale_logistic.service.manager;
//
//import com.logistic.digitale_logistic.dto.PoLineDTO;
//import com.logistic.digitale_logistic.dto.PurchaseOrderDTO;
//import com.logistic.digitale_logistic.entity.*;
//import com.logistic.digitale_logistic.enums.PurchaseOrderStatus;
//import com.logistic.digitale_logistic.exceptions.BusinessException;
//import com.logistic.digitale_logistic.mapper.PurchaseOrderMapper;
//import com.logistic.digitale_logistic.repository.*;
//import com.logistic.digitale_logistic.service.client.BackorderFulfillmentService;
//import com.logistic.digitale_logistic.service.warehouse_manager.PurchaseOrderService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class PurchaseOrderServiceTest {
//
//    @Mock private PurchaseOrderRepository purchaseOrderRepository;
//    @Mock private PoLineRepository poLineRepository;
//    @Mock private InventoryRepository inventoryRepository;
//    @Mock private InventoryMovementRepository inventoryMovementRepository;
//    @Mock private SupplierRepository supplierRepository;
//    @Mock private WareHouseRepository warehouseRepository;
//    @Mock private ProductRepository productRepository;
//    @Mock private PurchaseOrderMapper purchaseOrderMapper;
//    @Mock private BackorderFulfillmentService backorderFulfillmentService;
//
//    @InjectMocks
//    private PurchaseOrderService
//            purchaseOrderService;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // ================== TEST 1: CREATE PURCHASE ORDER SUCCESS ==================
//    @Test
//    void testCreatePurchaseOrder_Success() {
//        PurchaseOrderDTO dto = new PurchaseOrderDTO();
//        dto.setSupplierId(1L);
//        dto.setWarehouseId(10L);
//        dto.setOrderDate(LocalDate.now());
//
//        PoLineDTO lineDTO = new PoLineDTO();
//        lineDTO.setProductId(5L);
//        lineDTO.setOrderedQuantity(10);
//        lineDTO.setUnitCost(BigDecimal.valueOf(100.0));
//
//        dto.setLines(List.of(lineDTO));
//
//        Supplier supplier = new Supplier();
//        supplier.setId(1L);
//
//        Warehouse warehouse = new Warehouse();
//        warehouse.setId(10L);
//
//        Product product = new Product();
//        product.setId(5L);
//
//        PurchaseOrder savedPO = new PurchaseOrder();
//        savedPO.setId(99L);
//        savedPO.setStatus(PurchaseOrderStatus.DRAFT);
//
//        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
//        when(warehouseRepository.findById(10L)).thenReturn(Optional.of(warehouse));
//        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
//        when(purchaseOrderRepository.save(any())).thenReturn(savedPO);
//        when(purchaseOrderMapper.toDTO(savedPO)).thenReturn(new PurchaseOrderDTO());
//
//        PurchaseOrderDTO result = purchaseOrderService.createPurchaseOrder(dto);
//
//        assertNotNull(result);
//        verify(purchaseOrderRepository, times(2)).save(any());
//    }
//
//    // ================== TEST 2: CREATE PO → SUPPLIER NOT FOUND ==================
//    @Test
//    void testCreatePurchaseOrder_SupplierNotFound() {
//        PurchaseOrderDTO dto = new PurchaseOrderDTO();
//        dto.setSupplierId(3L);
//
//        when(supplierRepository.findById(3L)).thenReturn(Optional.empty());
//
//        BusinessException ex = assertThrows(BusinessException.class,
//                () -> purchaseOrderService.createPurchaseOrder(dto));
//
//        assertEquals("Supplier not found", ex.getMessage());
//    }
//
//    // ================== TEST 3: APPROVE PO SUCCESS ==================
//    @Test
//    void testApprovePurchaseOrder_Success() {
//        PurchaseOrder po = new PurchaseOrder();
//        po.setId(1L);
//        po.setStatus(PurchaseOrderStatus.DRAFT);
//
//        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));
//        when(purchaseOrderRepository.save(any())).thenReturn(po);
//        when(purchaseOrderMapper.toDTO(any())).thenReturn(new PurchaseOrderDTO());
//
//        PurchaseOrderDTO result = purchaseOrderService.approvePurchaseOrder(1L);
//
//        assertNotNull(result);
//        assertEquals(PurchaseOrderStatus.APPROVED, po.getStatus());
//    }
//
//    // ================== TEST 4: APPROVE FAIL → INVALID STATUS ==================
//    @Test
//    void testApprovePurchaseOrder_InvalidStatus() {
//        PurchaseOrder po = new PurchaseOrder();
//        po.setStatus(PurchaseOrderStatus.RECEIVED);
//
//        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));
//
//        BusinessException ex = assertThrows(BusinessException.class,
//                () -> purchaseOrderService.approvePurchaseOrder(1L));
//
//        assertEquals("Cannot approve: Purchase Order is not in DRAFT status", ex.getMessage());
//    }
//
//    // ================== TEST 5: RECEIVE ENTIRE PO SUCCESS ==================
//    @Test
//    void testReceiveEntirePurchaseOrder_Success() {
//        PurchaseOrder po = new PurchaseOrder();
//        po.setId(1L);
//        po.setStatus(PurchaseOrderStatus.APPROVED);
//
//        Product product = new Product();
//        product.setId(5L);
//
//        Warehouse warehouse = new Warehouse();
//        warehouse.setId(10L);
//
//        PoLine line = new PoLine();
//        line.setId(100L);
//        line.setProduct(product);
//        line.setOrderedQuantity(20);
//        line.setReceivedQuantity(0);
//
//        po.setWarehouse(warehouse);
//        po.setLines(new ArrayList<>(List.of(line)));
//
//        Inventory inventory = new Inventory();
//        inventory.setQtyOnHand(5);
//
//        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));
//        when(inventoryRepository.findByProduct_IdAndWarehouse_Id(5L, 10L))
//                .thenReturn(Optional.of(inventory));
//        when(inventoryRepository.save(any())).thenReturn(inventory);
//        when(purchaseOrderRepository.save(any())).thenReturn(po);
//        when(purchaseOrderMapper.toDTO(po)).thenReturn(new PurchaseOrderDTO());
//
//        PurchaseOrderDTO result = purchaseOrderService.receiveEntirePurchaseOrder(1L);
//
//        assertNotNull(result);
//        assertEquals(PurchaseOrderStatus.RECEIVED, po.getStatus());
//        assertEquals(25, inventory.getQtyOnHand()); // 5 existed + 20 received
//    }
//}
