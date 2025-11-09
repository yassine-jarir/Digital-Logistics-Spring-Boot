package com.logistic.digitale_logistic.controller.Admin;

import com.logistic.digitale_logistic.dto.SupplierDTO;
import com.logistic.digitale_logistic.service.Admin.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    // 1️⃣ Get all suppliers
    @GetMapping
    public List<SupplierDTO> getAllSuppliers() {
        return supplierService.getAll();
    }

    // 2️⃣ Get active suppliers only
    @GetMapping("/active")
    public List<SupplierDTO> getActiveSuppliers() {
        return supplierService.getActiveSuppliers();
    }

    // 3️⃣ Get supplier by ID
    @GetMapping("/{id}")
    public SupplierDTO getSupplierById(@PathVariable Long id) {
        return supplierService.getSupplierById(id);
    }

    // 4️⃣ Create a new supplier
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupplierDTO createSupplier(@RequestBody SupplierDTO supplierDTO) {
        return supplierService.createSupplier(supplierDTO);
    }

    // 5️⃣ Update an existing supplier
    @PutMapping("/{id}")
    public SupplierDTO updateSupplier(@PathVariable Long id, @RequestBody SupplierDTO supplierDTO) {
        return supplierService.updateSupplier(id, supplierDTO);
    }

    // 6️⃣ Delete supplier
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
    }

    // 7️⃣ Deactivate supplier (soft delete)
    @PatchMapping("/{id}/deactivate")
    public SupplierDTO deactivateSupplier(@PathVariable Long id) {
        return supplierService.deactivateSupplier(id);
    }
}
