package com.logistic.digitale_logistic.controller.Client;

import com.logistic.digitale_logistic.dto.WarehouseListDTO;
import com.logistic.digitale_logistic.service.client.WarehouseViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client/warehouses")
@RequiredArgsConstructor
@Tag(name = "Client - Warehouses", description = "Client endpoints for viewing warehouses and available inventory")
@SecurityRequirement(name = "Bearer Authentication")
public class ClientWarehouseController {

    private final WarehouseViewService warehouseViewService;
    /**
     * Get all warehouses with their available products and inventory levels
     *
     * Returns:
     * - All active warehouses
     * - Products in each warehouse with:
     *   - qtyOnHand (total stock)
     *   - qtyReserved (reserved for other orders)
     *   - qtyAvailable (qtyOnHand - qtyReserved)
     *
     * @return list of warehouses with product inventory
     */
    @Operation(
            summary = "Get all warehouses with inventory",
            description = "Retrieve all warehouses with their available inventory. Shows quantity on hand, reserved, and available for each product"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved warehouses with inventory"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Client role required")
    })
    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    @ResponseStatus(HttpStatus.OK)
    public List<WarehouseListDTO> getAllWarehouses() {

        return warehouseViewService.getAllWarehousesWithInventory();
    }

    /**
     * Get a specific warehouse with its inventory
     *
     * @param id the warehouse ID
     * @return warehouse with product inventory
     */
    @Operation(
            summary = "Get warehouse by ID with inventory",
            description = "Retrieve a specific warehouse with its current inventory levels"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved warehouse"),
            @ApiResponse(responseCode = "404", description = "Warehouse not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Client role required")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    @ResponseStatus(HttpStatus.OK)
    public WarehouseListDTO getWarehouseById(
            @Parameter(description = "Warehouse ID", required = true, example = "1")
            @PathVariable Long id) {
        return warehouseViewService.getWarehouseWithInventory(id);
    }
}
