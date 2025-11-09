package com.logistic.digitale_logistic.service.Admin;

import com.logistic.digitale_logistic.dto.InventoryDTO;
import com.logistic.digitale_logistic.entity.Inventory;
import com.logistic.digitale_logistic.entity.Product;
import com.logistic.digitale_logistic.entity.Warehouse;
import com.logistic.digitale_logistic.mapper.InventoryMapper;
import com.logistic.digitale_logistic.repository.InventoryRepository;
import com.logistic.digitale_logistic.repository.ProductRepository;
import com.logistic.digitale_logistic.repository.WareHouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final WareHouseRepository warehouseRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductRepository productRepository;

    public InventoryService(InventoryRepository inventoryRepository, WareHouseRepository warehouseRepository, InventoryMapper inventoryMapper, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.warehouseRepository = warehouseRepository;
        this.inventoryMapper = inventoryMapper;
        this.productRepository = productRepository;
    }
    public InventoryDTO getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        return inventoryMapper.toDTO(inventory);
    }

    public List<InventoryDTO> getAll() {
        return inventoryRepository.findAll().stream()
                .map(inventoryMapper::toDTO)
                .toList();
    }

    public InventoryDTO createInventory(InventoryDTO dto) {
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Inventory inventory = inventoryMapper.toEntity(dto);
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);

        Inventory saved = inventoryRepository.save(inventory);
        return inventoryMapper.toDTO(saved);
    }

    public InventoryDTO updateInventory(Long id, InventoryDTO dto) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inventory.setQtyOnHand(dto.getQtyOnHand());
        inventory.setQtyReserved(dto.getQtyReserved());

        return inventoryMapper.toDTO(inventoryRepository.save(inventory));
    }

    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }

    public List<InventoryDTO> getInventoryByWarehouse(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        List<Inventory> inventories = inventoryRepository.findByWarehouse(warehouse);

        return inventories.stream()
                .map(inventoryMapper::toDTO)
                .toList();
    }
}
