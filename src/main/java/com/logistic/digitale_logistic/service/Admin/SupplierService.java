package com.logistic.digitale_logistic.service.Admin;

import com.logistic.digitale_logistic.dto.SupplierDTO;
import com.logistic.digitale_logistic.entity.Supplier;
import com.logistic.digitale_logistic.mapper.SupplierMapper;
import com.logistic.digitale_logistic.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public List<SupplierDTO> getAll() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toDTO)
                .toList();
    }

    public List<SupplierDTO> getActiveSuppliers() {
        return supplierRepository.findByActiveTrue().stream()
                .map(supplierMapper::toDTO)
                .toList();
    }

    public SupplierDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return supplierMapper.toDTO(supplier);
    }

    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        supplier.setCreatedAt(LocalDateTime.now());
        
        if (supplier.getActive() == null) {
            supplier.setActive(true);
        }
        
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDTO(savedSupplier);
    }

    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        existingSupplier.setName(supplierDTO.getName());
        existingSupplier.setEmail(supplierDTO.getEmail());
        existingSupplier.setPhone(supplierDTO.getPhone());
        existingSupplier.setAddress(supplierDTO.getAddress());
        
        if (supplierDTO.getActive() != null) {
            existingSupplier.setActive(supplierDTO.getActive());
        }

        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        return supplierMapper.toDTO(updatedSupplier);
    }

    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        supplierRepository.delete(supplier);
    }

    public SupplierDTO deactivateSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        supplier.setActive(false);
        Supplier deactivatedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDTO(deactivatedSupplier);
    }
}

