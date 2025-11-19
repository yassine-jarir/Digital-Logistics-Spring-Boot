package com.logistic.digitale_logistic.service.client;

import com.logistic.digitale_logistic.dto.ProductInventoryDTO;
import com.logistic.digitale_logistic.dto.WarehouseDTO;
import com.logistic.digitale_logistic.entity.Inventory;
import com.logistic.digitale_logistic.entity.Product;
import com.logistic.digitale_logistic.entity.Warehouse;
import com.logistic.digitale_logistic.repository.InventoryRepository;
import com.logistic.digitale_logistic.repository.WareHouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseViewService {

    private final WareHouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;

    /**
     * Get all active warehouses with their available products and inventory levels
     *
     * @return list of warehouses with product inventory
     */
    @Transactional(readOnly = true)
    public List<WarehouseDTO> getAllWarehousesWithInventory() {
        // Get all active warehouses
        List<Warehouse> warehouses = warehouseRepository.findAll().stream()
                .filter(Warehouse::getActive)
                .toList();

        List<WarehouseDTO> warehouseDTOs = new ArrayList<>();

        for (Warehouse warehouse : warehouses) {
            // Get inventory for this warehouse
            List<Inventory> inventoryList = inventoryRepository.findByWarehouse(warehouse);

            // Convert inventory to ProductInventoryDTO (only active products)
            List<ProductInventoryDTO> productInventoryDTOs = inventoryList.stream()
                    .filter(inv -> inv.getProduct().getActive()) // Only active products
                    .map(this::toProductInventoryDTO)
                    .collect(Collectors.toList());

            // Build warehouse DTO
            WarehouseDTO warehouseDTO = WarehouseDTO.builder()
                    .id(warehouse.getId())
                    .name(warehouse.getName())
                    .location(warehouse.getLocation())
                    .active(warehouse.getActive())
                    .products(productInventoryDTOs)
                    .build();

            warehouseDTOs.add(warehouseDTO);
        }

        return warehouseDTOs;
    }

    /**
     * Get a specific warehouse with its inventory
     *
     * @param warehouseId the warehouse ID
     * @return warehouse with product inventory
     */
    @Transactional(readOnly = true)
    public WarehouseDTO getWarehouseWithInventory(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found with ID: " + warehouseId));

        if (!warehouse.getActive()) {
            throw new IllegalArgumentException("Warehouse is not active");
        }

        // Get inventory for this warehouse
        List<Inventory> inventoryList = inventoryRepository.findByWarehouse(warehouse);

        // Convert inventory to ProductInventoryDTO (only active products)
        List<ProductInventoryDTO> productInventoryDTOs = inventoryList.stream()
                .filter(inv -> inv.getProduct().getActive()) // Only active products
                .map(this::toProductInventoryDTO)
                .collect(Collectors.toList());

        return WarehouseDTO.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .active(warehouse.getActive())
                .products(productInventoryDTOs)
                .build();
    }

    /**
     * Convert Inventory entity to ProductInventoryDTO
     */
    private ProductInventoryDTO toProductInventoryDTO(Inventory inventory) {
        Product product = inventory.getProduct();

        return ProductInventoryDTO.builder()
                .productId(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .category(product.getCategory())
                .unitPrice(product.getSellingPrice())
                .qtyOnHand(inventory.getQtyOnHand())
                .qtyReserved(inventory.getQtyReserved())
                .qtyAvailable(inventory.getQtyAvailable()) // DB-calculated field
                .build();
    }
}

