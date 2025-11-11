package com.logistic.digitale_logistic.controller.Client;

import com.logistic.digitale_logistic.dto.WarehouseDTO;
import com.logistic.digitale_logistic.service.client.WarehouseViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client/warehouses")
@RequiredArgsConstructor
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
    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    @ResponseStatus(HttpStatus.OK)
    public List<WarehouseDTO> getAllWarehouses() {

        return warehouseViewService.getAllWarehousesWithInventory();
    }

    /**
     * Get a specific warehouse with its inventory
     *
     * @param id the warehouse ID
     * @return warehouse with product inventory
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    @ResponseStatus(HttpStatus.OK)
    public WarehouseDTO getWarehouseById(@PathVariable Long id) {
        return warehouseViewService.getWarehouseWithInventory(id);
    }
}

