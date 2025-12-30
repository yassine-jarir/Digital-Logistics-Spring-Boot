//package com.logistic.digitale_logistic.service.Admin;
//
//import com.logistic.digitale_logistic.dto.InventoryDTO;
//import com.logistic.digitale_logistic.entity.Inventory;
//import com.logistic.digitale_logistic.entity.Product;
//import com.logistic.digitale_logistic.entity.Warehouse;
//import com.logistic.digitale_logistic.mapper.InventoryMapper;
//import com.logistic.digitale_logistic.repository.InventoryRepository;
//import com.logistic.digitale_logistic.repository.ProductRepository;
//import com.logistic.digitale_logistic.repository.WareHouseRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class InventoryServiceTest {
//
//    @Mock
//    private InventoryRepository inventoryRepository;
//
//    @Mock
//    private WareHouseRepository warehouseRepository;
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private InventoryMapper inventoryMapper;
//
//    @InjectMocks
//    private InventoryService inventoryService;
//
//    @Test
//    void testCreateInventory() {
//        // Given
//        InventoryDTO inputDTO = new InventoryDTO();
//        inputDTO.setWarehouseId(1L);
//        inputDTO.setProductId(1L);
//
//        Warehouse warehouse = new Warehouse();
//        warehouse.setId(1L);
//
//        Product product = new Product();
//        product.setId(1L);
//
//        Inventory inventoryEntity = new Inventory();
//        Inventory savedInventory = new Inventory();
//        savedInventory.setId(10L);
//
//        InventoryDTO resultDTO = new InventoryDTO();
//        resultDTO.setId(10L);
//
//        // Mock repository and mapper calls
//        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(inventoryMapper.toEntity(inputDTO)).thenReturn(inventoryEntity);
//        when(inventoryRepository.save(inventoryEntity)).thenReturn(savedInventory);
//        when(inventoryMapper.toDTO(savedInventory)).thenReturn(resultDTO);
//
//        // When
//        InventoryDTO result = inventoryService.createInventory(inputDTO);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(10L, result.getId());
//
//        verify(warehouseRepository).findById(1L);
//        verify(productRepository).findById(1L);
//        verify(inventoryRepository).save(inventoryEntity);
//        verify(inventoryMapper).toDTO(savedInventory);
//    }
//}
