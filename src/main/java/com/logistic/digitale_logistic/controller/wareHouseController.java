package com.logistic.digitale_logistic.controller;

import com.logistic.digitale_logistic.dto.WareHouseDTO;
import com.logistic.digitale_logistic.service.Admin.wareHouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/warehouses")
@Tag(name = "Admin - Warehouses", description = "Admin endpoints for managing warehouses")
@SecurityRequirement(name = "Bearer Authentication")
public class wareHouseController {

    private final wareHouseService warehouseService;

    public wareHouseController(wareHouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @Operation(
            summary = "Create warehouse",
            description = "Create a new warehouse in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Warehouse created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid warehouse data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public WareHouseDTO create(@RequestBody WareHouseDTO dto) {
        return warehouseService.create(dto);
    }

    @Operation(
            summary = "Get all warehouses",
            description = "Retrieve all warehouses in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all warehouses"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public List<WareHouseDTO> getAll() {
        return warehouseService.getAll();
    }

    @Operation(
            summary = "Get warehouse by ID",
            description = "Retrieve a specific warehouse by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Warehouse found"),
            @ApiResponse(responseCode = "404", description = "Warehouse not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    public WareHouseDTO getById(
            @Parameter(description = "Warehouse ID", required = true, example = "1")
            @PathVariable Long id) {
        return warehouseService.getById(id);
    }

    @Operation(
            summary = "Update warehouse",
            description = "Update an existing warehouse"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Warehouse updated successfully"),
            @ApiResponse(responseCode = "404", description = "Warehouse not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public WareHouseDTO update(
            @Parameter(description = "Warehouse ID", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody WareHouseDTO dto) {
        return warehouseService.update(id, dto);
    }

    @Operation(
            summary = "Delete warehouse",
            description = "Delete a warehouse from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Warehouse deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Warehouse not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "Warehouse ID", required = true, example = "1")
            @PathVariable Long id) {
        warehouseService.delete(id);
    }
}
