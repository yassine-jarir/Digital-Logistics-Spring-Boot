package com.logistic.digitale_logistic.service.manager;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Collections;

import com.logistic.digitale_logistic.dto.PoLineDTO;
import com.logistic.digitale_logistic.dto.PurchaseOrderDTO;
import com.logistic.digitale_logistic.entity.Product;
import com.logistic.digitale_logistic.entity.PurchaseOrder;
import com.logistic.digitale_logistic.entity.Supplier;
import com.logistic.digitale_logistic.entity.Warehouse;
import com.logistic.digitale_logistic.exceptions.BusinessException;
import com.logistic.digitale_logistic.mapper.PurchaseOrderMapper;
import com.logistic.digitale_logistic.repository.ProductRepository;
import com.logistic.digitale_logistic.repository.PurchaseOrderRepository;
import com.logistic.digitale_logistic.repository.SupplierRepository;
import com.logistic.digitale_logistic.repository.WareHouseRepository;
import com.logistic.digitale_logistic.service.warehouse_manager.PurchaseOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PurchaseOrderServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private WareHouseRepository warehouseRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private PurchaseOrderMapper purchaseOrderMapper;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreatePurchaseOrder_Success() {
        // Input DTO
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setSupplierId(1L);
        dto.setWarehouseId(1L);
        dto.setOrderDate(LocalDate.now());

        PoLineDTO lineDTO = new PoLineDTO();
        lineDTO.setProductId(100L);
        lineDTO.setOrderedQuantity(10);
        lineDTO.setUnitCost(BigDecimal.valueOf(50.0));
        dto.setLines(Collections.singletonList(lineDTO));

        // Mock supplier, warehouse, product
        Supplier supplier = new Supplier();
        supplier.setId(1L);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);

        Product product = new Product();
        product.setId(100L);

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));

        // Mock save for PurchaseOrder
        PurchaseOrder savedPo = new PurchaseOrder();
        savedPo.setId(1L);
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(savedPo);

        // Mock mapper
        PurchaseOrderDTO savedDto = new PurchaseOrderDTO();
        savedDto.setId(1L);
        when(purchaseOrderMapper.toDTO(savedPo)).thenReturn(savedDto);

        // Call the method
        PurchaseOrderDTO result = purchaseOrderService.createPurchaseOrder(dto);

        // Verify results
        assertNotNull(result);
        assertEquals(1L, result.getId());

        // Verify repository interactions
        verify(supplierRepository).findById(1L);
        verify(warehouseRepository).findById(1L);
        verify(productRepository).findById(100L);
        verify(purchaseOrderRepository, times(2)).save(any(PurchaseOrder.class));
    }

    @Test
    public void testCreatePurchaseOrder_SupplierNotFound() {
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setSupplierId(1L);
        dto.setWarehouseId(1L);

        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());
// assertThrows checks that the method throws the expected exception.
        BusinessException exception = assertThrows(BusinessException.class,
                () -> purchaseOrderService.createPurchaseOrder(dto));

        assertEquals("Supplier not found", exception.getMessage());
    }

    @Test
    public void testCreatePurchaseOrder_ProductNotFound() {
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setSupplierId(1L);
        dto.setWarehouseId(1L);

        PoLineDTO lineDTO = new PoLineDTO();
        lineDTO.setProductId(100L);
        lineDTO.setOrderedQuantity(10);
        dto.setLines(Collections.singletonList(lineDTO));

        Supplier supplier = new Supplier();
        Warehouse warehouse = new Warehouse();

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(productRepository.findById(100L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> purchaseOrderService.createPurchaseOrder(dto));

        assertEquals("Product not found: 100", exception.getMessage());
    }
}
