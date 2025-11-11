package com.logistic.digitale_logistic.controller.Admin;

import com.logistic.digitale_logistic.dto.InventoryDTO;
import com.logistic.digitale_logistic.service.Admin.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public List<InventoryDTO> getAllInventories() {
        return inventoryService.getAll();
    }

    // 1️⃣ Get all inventory for a warehouse
    @GetMapping("/warehouse/{warehouseId}")
    public List<InventoryDTO> getInventoryByWarehouse(@PathVariable Long warehouseId) {
        return inventoryService.getInventoryByWarehouse(warehouseId);
    }

    // 2️⃣ Get inventory by ID
    @GetMapping("/{id}")
    public InventoryDTO getInventoryById(@PathVariable Long id) {
        return inventoryService.getInventoryById(id);
    }

    // 3️⃣ Create a new inventory record
    @PostMapping
    public InventoryDTO createInventory(@RequestBody InventoryDTO inventoryDTO) {
        return inventoryService.createInventory(inventoryDTO);
    }

    // 4️⃣ Update an existing inventory
    @PutMapping("/{id}")
    public InventoryDTO updateInventory(@PathVariable Long id, @RequestBody InventoryDTO inventoryDTO) {
        return inventoryService.updateInventory(id, inventoryDTO);
    }

    // 5️⃣ Delete inventory
    @DeleteMapping("/{id}")
    public void deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
    }
}
