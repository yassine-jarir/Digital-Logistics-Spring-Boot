//package com.logistic.digitale_logistic.service.Admin;
//
//import com.logistic.digitale_logistic.dto.WareHouseDTO;
//import com.logistic.digitale_logistic.entity.Warehouse;
//import com.logistic.digitale_logistic.mapper.wareHouseMapper;
//import com.logistic.digitale_logistic.repository.WareHouseRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class WarehouseServiceTest {
//
//    @Mock
//    private WareHouseRepository warehouseRepository;
//    @Mock
//    private wareHouseMapper warehouseMapper;
//
//    @InjectMocks
//    private wareHouseService wareHouseService;
//
//    // Dummy data for testing
//    private final Long warehouseId = 1L;
//    private final String warehouseName = "Central Warehouse";
//    private final String warehouseLocation = "New York";
//
//    private Warehouse dummyEntity;
//    private WareHouseDTO dummyDto;
//
//    @BeforeEach
//    void setUp() {
//        dummyEntity = new Warehouse();
//        dummyEntity.setId(warehouseId);
//        dummyEntity.setName(warehouseName);
//        dummyEntity.setLocation(warehouseLocation);
//        dummyEntity.setActive(true);
//
//        dummyDto = new WareHouseDTO();
//        dummyDto.setName(warehouseName);
//        dummyDto.setLocation(warehouseLocation);
//        dummyDto.setActive(true);
//    }
//
//    // =========================================================================
//    // 1. CREATE TESTS
//    // =========================================================================
//
//    @Test
//    void create_Success() {
//        // Mock mapper to convert DTO to Entity
//        when(warehouseMapper.toEntity(dummyDto)).thenReturn(dummyEntity);
//        // Mock repository to check if name exists (return false/not exists)
//        when(warehouseRepository.existsByName(warehouseName)).thenReturn(false);
//        // Mock repository save call
//        when(warehouseRepository.save(dummyEntity)).thenReturn(dummyEntity);
//        // Mock mapper to convert saved Entity back to DTO
//        when(warehouseMapper.toDto(dummyEntity)).thenReturn(dummyDto);
//
//        WareHouseDTO result = wareHouseService.create(dummyDto);
//
//        assertNotNull(result);
//        assertEquals(warehouseName, result.getName());
//        verify(warehouseRepository, times(1)).existsByName(warehouseName);
//        verify(warehouseRepository, times(1)).save(dummyEntity);
//    }
//
//    @Test
//    void create_ThrowsExceptionIfNameExists() {
//        // Mock repository to check if name exists (return true/exists)
//        when(warehouseRepository.existsByName(warehouseName)).thenReturn(true);
//
//        // Assert that the RuntimeException is thrown
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            wareHouseService.create(dummyDto);
//        });
//
//        assertEquals("Warehouse with name 'Central Warehouse' already exists", exception.getMessage());
//        verify(warehouseRepository, never()).save(any(Warehouse.class));
//    }
//
//    // =========================================================================
//    // 2. GET ALL TESTS
//    // =========================================================================
//
//    @Test
//    void getAll_SuccessWithData() {
//        // Mock repository find all call
//        when(warehouseRepository.findAll()).thenReturn(List.of(dummyEntity));
//        // Mock mapper call
//        when(warehouseMapper.toDto(dummyEntity)).thenReturn(dummyDto);
//
//        List<WareHouseDTO> result = wareHouseService.getAll();
//
//        assertFalse(result.isEmpty());
//        assertEquals(1, result.size());
//        verify(warehouseRepository, times(1)).findAll();
//        verify(warehouseMapper, times(1)).toDto(dummyEntity);
//    }
//
//    @Test
//    void getAll_SuccessNoData() {
//        // Mock repository find all call to return empty list
//        when(warehouseRepository.findAll()).thenReturn(Collections.emptyList());
//
//        List<WareHouseDTO> result = wareHouseService.getAll();
//
//        assertTrue(result.isEmpty());
//        verify(warehouseRepository, times(1)).findAll();
//        verify(warehouseMapper, never()).toDto(any(Warehouse.class));
//    }
//
//    // =========================================================================
//    // 3. GET BY ID TESTS
//    // =========================================================================
//
//    @Test
//    void getById_Success() {
//        // Mock repository find by id call (found)
//        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(dummyEntity));
//        // Mock mapper call
//        when(warehouseMapper.toDto(dummyEntity)).thenReturn(dummyDto);
//
//        WareHouseDTO result = wareHouseService.getById(warehouseId);
//
//        assertNotNull(result);
//        assertEquals(warehouseName, result.getName());
//        verify(warehouseRepository, times(1)).findById(warehouseId);
//    }
//
//    @Test
//    void getById_ThrowsExceptionIfNotFound() {
//        // Mock repository find by id call (not found)
//        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());
//
//        // Assert that the RuntimeException is thrown
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            wareHouseService.getById(warehouseId);
//        });
//
//        assertEquals("Warehouse not found", exception.getMessage());
//    }
//
//    // =========================================================================
//    // 4. UPDATE TESTS
//
//    @Test
//    void update_ThrowsExceptionIfNotFound() {
//        WareHouseDTO updateDto = new WareHouseDTO("New Name", "New Location", false);
//
//        // Mock repository find by id call (not found)
//        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());
//
//        // Assert that the RuntimeException is thrown
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            wareHouseService.update(warehouseId, updateDto);
//        });
//
//        assertEquals("Warehouse not found", exception.getMessage());
//        verify(warehouseRepository, never()).save(any(Warehouse.class));
//    }
//
//    // =========================================================================
//    // 5. DELETE TESTS
//    // =========================================================================
//
//    @Test
//    void delete_Success() {
//        // Mock repository exists by id call (return true/exists)
//        when(warehouseRepository.existsById(warehouseId)).thenReturn(true);
//        // Do nothing when delete is called
//        doNothing().when(warehouseRepository).deleteById(warehouseId);
//
//        // Execute
//        wareHouseService.delete(warehouseId);
//
//        // Verify
//        verify(warehouseRepository, times(1)).existsById(warehouseId);
//        verify(warehouseRepository, times(1)).deleteById(warehouseId);
//    }
//
//    @Test
//    void delete_ThrowsExceptionIfNotFound() {
//        // Mock repository exists by id call (return false/not found)
//        when(warehouseRepository.existsById(warehouseId)).thenReturn(false);
//
//        // Assert that the RuntimeException is thrown
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            wareHouseService.delete(warehouseId);
//        });
//
//        assertEquals("Warehouse not found", exception.getMessage());
//        verify(warehouseRepository, never()).deleteById(anyLong());
//    }
//}