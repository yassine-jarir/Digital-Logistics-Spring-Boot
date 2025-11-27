package com.logistic.digitale_logistic.controller;

import com.logistic.digitale_logistic.dto.InventoryDTO;
import com.logistic.digitale_logistic.service.Admin.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Admin - Inventory", description = "Admin endpoints for managing inventory across warehouses")
@SecurityRequirement(name = "Bearer Authentication")
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(
            summary = "Get all inventories",
            description = "Retrieve all inventory records across all warehouses"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all inventories"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public List<InventoryDTO> getAllInventories() {
        return inventoryService.getAll();
    }

    // 1️⃣ Get all inventory for a warehouse
    @Operation(
            summary = "Get inventory by warehouse",
            description = "Retrieve all inventory records for a specific warehouse"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved warehouse inventory"),
            @ApiResponse(responseCode = "404", description = "Warehouse not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/warehouse/{warehouseId}")
    public List<InventoryDTO> getInventoryByWarehouse(
            @Parameter(description = "Warehouse ID", required = true, example = "1")
            @PathVariable Long warehouseId) {
        return inventoryService.getInventoryByWarehouse(warehouseId);
    }

    // 2️⃣ Get inventory by ID
    @Operation(
            summary = "Get inventory by ID",
            description = "Retrieve a specific inventory record by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved inventory"),
            @ApiResponse(responseCode = "404", description = "Inventory not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    public InventoryDTO getInventoryById(
            @Parameter(description = "Inventory ID", required = true, example = "1")
            @PathVariable Long id) {
        return inventoryService.getInventoryById(id);
    }

    // 3️⃣ Create a new inventory record
    @Operation(
            summary = "Create inventory record",
            description = "Create a new inventory record for a product in a warehouse"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid inventory data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public InventoryDTO createInventory(@RequestBody InventoryDTO inventoryDTO) {
        return inventoryService.createInventory(inventoryDTO);
    }

    // 4️⃣ Update an existing inventory
    @Operation(
            summary = "Update inventory",
            description = "Update an existing inventory record"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory updated successfully"),
            @ApiResponse(responseCode = "404", description = "Inventory not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public InventoryDTO updateInventory(
            @Parameter(description = "Inventory ID", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody InventoryDTO inventoryDTO) {
        return inventoryService.updateInventory(id, inventoryDTO);
    }

    // 5️⃣ Delete inventory
    @Operation(
            summary = "Delete inventory",
            description = "Delete an inventory record"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Inventory not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    public void deleteInventory(
            @Parameter(description = "Inventory ID", required = true, example = "1")
            @PathVariable Long id) {
        inventoryService.deleteInventory(id);
    }
}
