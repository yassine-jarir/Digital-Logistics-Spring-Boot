package com.logistic.digitale_logistic.service.client;

import com.logistic.digitale_logistic.dto.WarehouseProductDTO;
import com.logistic.digitale_logistic.dto.WarehouseWithProductsDTO;
import com.logistic.digitale_logistic.entity.Inventory;
import com.logistic.digitale_logistic.entity.Warehouse;
import com.logistic.digitale_logistic.repository.InventoryRepository;
import com.logistic.digitale_logistic.repository.WareHouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientWarehouseService {

    private final WareHouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<WarehouseWithProductsDTO> getAllWarehousesWithProducts() {
        List<Warehouse> warehouses = warehouseRepository.findAll();

        return warehouses.stream()
                .map(this::mapToWarehouseWithProducts)
                .collect(Collectors.toList());
    }

    private WarehouseWithProductsDTO mapToWarehouseWithProducts(Warehouse warehouse) {
        // Get all inventory for this warehouse with active products only
        List<Inventory> inventories = inventoryRepository.findAll().stream()
                .filter(inv -> inv.getWarehouse().getId().equals(warehouse.getId()))
                .filter(inv -> inv.getProduct().getActive())
                .collect(Collectors.toList());

        List<WarehouseProductDTO> products = inventories.stream()
                .map(this::mapToWarehouseProductDTO)
                .collect(Collectors.toList());

        return WarehouseWithProductsDTO.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .active(warehouse.getActive())
                .products(products)
                .build();
    }

    private WarehouseProductDTO mapToWarehouseProductDTO(Inventory inventory) {
        return WarehouseProductDTO.builder()
                .productId(inventory.getProduct().getId())
                .name(inventory.getProduct().getName())
                .sku(inventory.getProduct().getSku())
                .category(inventory.getProduct().getCategory())
                .unitPrice(inventory.getProduct().getSellingPrice())
                .availableQuantity(inventory.getQtyAvailable())
                .qtyOnHand(inventory.getQtyOnHand())
                .qtyReserved(inventory.getQtyReserved())
                .build();
    }
}

