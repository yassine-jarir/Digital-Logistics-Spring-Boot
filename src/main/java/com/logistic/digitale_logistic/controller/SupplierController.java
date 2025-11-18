package com.logistic.digitale_logistic.controller;

import com.logistic.digitale_logistic.dto.SupplierDTO;
import com.logistic.digitale_logistic.service.Admin.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Tag(name = "Admin - Suppliers", description = "Admin endpoints for managing suppliers")
@SecurityRequirement(name = "Bearer Authentication")
public class SupplierController {

    private final SupplierService supplierService;

    // 1️⃣ Get all suppliers
    @Operation(
            summary = "Get all suppliers",
            description = "Retrieve all suppliers in the system (active and inactive)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all suppliers"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public List<SupplierDTO> getAllSuppliers() {
        return supplierService.getAll();
    }

    // 2️⃣ Get active suppliers only
    @Operation(
            summary = "Get active suppliers",
            description = "Retrieve only active suppliers"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved active suppliers"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/active")
    public List<SupplierDTO> getActiveSuppliers() {
        return supplierService.getActiveSuppliers();
    }

    // 3️⃣ Get supplier by ID
    @Operation(
            summary = "Get supplier by ID",
            description = "Retrieve a specific supplier by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier found"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    public SupplierDTO getSupplierById(
            @Parameter(description = "Supplier ID", required = true, example = "1")
            @PathVariable Long id) {
        return supplierService.getSupplierById(id);
    }

    // 4️⃣ Create a new supplier
    @Operation(
            summary = "Create supplier",
            description = "Create a new supplier in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Supplier created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid supplier data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupplierDTO createSupplier(@RequestBody SupplierDTO supplierDTO) {
        return supplierService.createSupplier(supplierDTO);
    }

    // 5️⃣ Update an existing supplier
    @Operation(
            summary = "Update supplier",
            description = "Update an existing supplier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier updated successfully"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public SupplierDTO updateSupplier(
            @Parameter(description = "Supplier ID", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody SupplierDTO supplierDTO) {
        return supplierService.updateSupplier(id, supplierDTO);
    }

    // 6️⃣ Delete supplier
    @Operation(
            summary = "Delete supplier",
            description = "Permanently delete a supplier from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Supplier deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSupplier(
            @Parameter(description = "Supplier ID", required = true, example = "1")
            @PathVariable Long id) {
        supplierService.deleteSupplier(id);
    }

    // 7️⃣ Deactivate supplier (soft delete)
    @Operation(
            summary = "Deactivate supplier",
            description = "Deactivate a supplier (soft delete) - supplier remains in database but is marked inactive"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PatchMapping("/{id}/deactivate")
    public SupplierDTO deactivateSupplier(
            @Parameter(description = "Supplier ID", required = true, example = "1")
            @PathVariable Long id) {
        return supplierService.deactivateSupplier(id);
    }
}
