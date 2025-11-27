package com.logistic.digitale_logistic.service.Admin;

import com.logistic.digitale_logistic.dto.SupplierDTO;
import com.logistic.digitale_logistic.entity.Supplier;
import com.logistic.digitale_logistic.mapper.SupplierMapper;
import com.logistic.digitale_logistic.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier supplier;
    private SupplierDTO supplierDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        supplier = Supplier.builder()
                .id(1L)
                .name("Supplier1")
                .email("supplier1@example.com")
                .phone("123456789")
                .address("Address 1")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        supplierDTO = SupplierDTO.builder()
                .id(1L)
                .name("Supplier1")
                .email("supplier1@example.com")
                .phone("123456789")
                .address("Address 1")
                .active(true)
                .build();
    }

    @Test
    void testGetAll() {
        when(supplierRepository.findAll()).thenReturn(List.of(supplier));
        when(supplierMapper.toDTO(supplier)).thenReturn(supplierDTO);

        List<SupplierDTO> result = supplierService.getAll();
        assertEquals(1, result.size());
        assertEquals("Supplier1", result.get(0).getName());
    }

    @Test
    void testGetActiveSuppliers() {
        when(supplierRepository.findByActiveTrue()).thenReturn(List.of(supplier));
        when(supplierMapper.toDTO(supplier)).thenReturn(supplierDTO);

        List<SupplierDTO> result = supplierService.getActiveSuppliers();
        assertEquals(1, result.size());
        assertTrue(result.get(0).getActive());
    }

    @Test
    void testGetSupplierById_Success() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierMapper.toDTO(supplier)).thenReturn(supplierDTO);

        SupplierDTO result = supplierService.getSupplierById(1L);
        assertEquals("Supplier1", result.getName());
    }

    @Test
    void testGetSupplierById_NotFound() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                supplierService.getSupplierById(1L));
        assertEquals("Supplier not found", exception.getMessage());
    }

    @Test
    void testCreateSupplier() {
        when(supplierMapper.toEntity(supplierDTO)).thenReturn(supplier);
        when(supplierRepository.save(supplier)).thenReturn(supplier);
        when(supplierMapper.toDTO(supplier)).thenReturn(supplierDTO);

        SupplierDTO result = supplierService.createSupplier(supplierDTO);
        assertEquals("Supplier1", result.getName());
        verify(supplierRepository, times(1)).save(supplier);
    }

    @Test
    void testUpdateSupplier_Success() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.save(supplier)).thenReturn(supplier);
        when(supplierMapper.toDTO(supplier)).thenReturn(supplierDTO);

        supplierDTO.setName("Updated Name");
        SupplierDTO result = supplierService.updateSupplier(1L, supplierDTO);
        assertEquals("Updated Name", result.getName());
        verify(supplierRepository, times(1)).save(supplier);
    }

    @Test
    void testUpdateSupplier_NotFound() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                supplierService.updateSupplier(1L, supplierDTO));
        assertEquals("Supplier not found", exception.getMessage());
    }

    @Test
    void testDeleteSupplier_Success() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        supplierService.deleteSupplier(1L);
        verify(supplierRepository, times(1)).delete(supplier);
    }

    @Test
    void testDeleteSupplier_NotFound() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                supplierService.deleteSupplier(1L));
        assertEquals("Supplier not found", exception.getMessage());
    }

    @Test
    void testDeactivateSupplier() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.save(supplier)).thenReturn(supplier);
        when(supplierMapper.toDTO(supplier)).thenReturn(supplierDTO);

        SupplierDTO result = supplierService.deactivateSupplier(1L);
        assertFalse(supplier.getActive());
        assertEquals("Supplier1", result.getName());
        verify(supplierRepository, times(1)).save(supplier);
    }
}
