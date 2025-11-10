package com.logistic.digitale_logistic.service.warehouse_manager;

import com.logistic.digitale_logistic.dto.PoLineDTO;
import com.logistic.digitale_logistic.dto.PurchaseOrderDTO;
import com.logistic.digitale_logistic.dto.ReceiveLineDTO;
import com.logistic.digitale_logistic.entity.*;
import com.logistic.digitale_logistic.enums.MovementType;
import com.logistic.digitale_logistic.enums.PurchaseOrderStatus;
import com.logistic.digitale_logistic.exceptions.BusinessException;
import com.logistic.digitale_logistic.mapper.PurchaseOrderMapper;
import com.logistic.digitale_logistic.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PoLineRepository poLineRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final SupplierRepository supplierRepository;
    private final WareHouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;

    // ========== 1. CREATE PURCHASE ORDER ==========
    @Transactional
    public PurchaseOrderDTO createPurchaseOrder(PurchaseOrderDTO dto) {
        // Validate supplier
        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new BusinessException("Supplier not found"));

        // Validate warehouse
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new BusinessException("Warehouse not found"));

        // Create Purchase Order
        PurchaseOrder po = new PurchaseOrder();
        po.setPoNumber(generatePoNumber());
        po.setSupplier(supplier);
        po.setWarehouse(warehouse);
        po.setStatus(PurchaseOrderStatus.DRAFT.name());
        po.setOrderDate(dto.getOrderDate());
        po.setCreatedAt(LocalDateTime.now());
        po.setUpdatedAt(LocalDateTime.now());

        // Save PO first to get ID
        po = purchaseOrderRepository.save(po);

        // Create PO Lines
        if (dto.getLines() != null && !dto.getLines().isEmpty()) {
            for (PoLineDTO lineDTO : dto.getLines()) {
                Product product = productRepository.findById(lineDTO.getProductId())
                        .orElseThrow(() -> new BusinessException("Product not found: " + lineDTO.getProductId()));

                PoLine line = new PoLine();
                line.setPurchaseOrder(po);
                line.setProduct(product);
                line.setOrderedQuantity(lineDTO.getOrderedQuantity());
                line.setReceivedQuantity(0);
                line.setUnitCost(lineDTO.getUnitCost());

                po.getLines().add(line);
            }
        }

        po = purchaseOrderRepository.save(po);
        return purchaseOrderMapper.toDTO(po);
    }

    // ========== 2. APPROVE PURCHASE ORDER ==========
    @Transactional
    public PurchaseOrderDTO approvePurchaseOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Purchase Order not found"));

        // Validate status
        if (!PurchaseOrderStatus.DRAFT.name().equals(po.getStatus())) {
            throw new BusinessException("Cannot approve: Purchase Order is not in DRAFT status");
        }

        po.setStatus(PurchaseOrderStatus.APPROVED.name());
        po.setUpdatedAt(LocalDateTime.now());

        po = purchaseOrderRepository.save(po);
        return purchaseOrderMapper.toDTO(po);
    }

    // ========== 3. RECEIVE PURCHASE ORDER ==========
    @Transactional
    public PurchaseOrderDTO receivePurchaseOrder(Long id, List<ReceiveLineDTO> receivedLines) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Purchase Order not found"));

        // Validate status
        if (!PurchaseOrderStatus.APPROVED.name().equals(po.getStatus())) {
            throw new BusinessException("Cannot receive: Purchase Order must be APPROVED first");
        }

        // Process each received line
        for (ReceiveLineDTO receiveDTO : receivedLines) {
            PoLine poLine = po.getLines().stream()
                    .filter(line -> line.getId().equals(receiveDTO.getPoLineId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("PO Line not found: " + receiveDTO.getPoLineId()));

            Integer qtyToReceive = receiveDTO.getReceivedQuantity();

            // Validate quantity
            if (qtyToReceive <= 0) {
                throw new BusinessException("Received quantity must be greater than 0");
            }

            // Update PO Line
            poLine.setReceivedQuantity(poLine.getReceivedQuantity() + qtyToReceive);
            poLine.setReceivedDate(LocalDateTime.now());

            // Update Inventory (qtyOnHand)
            Inventory inventory = findOrCreateInventory(poLine.getProduct(), po.getWarehouse());
            inventory.setQtyOnHand(inventory.getQtyOnHand() + qtyToReceive);
            inventory.setUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(inventory);

            // Create Inventory Movement (INBOUND)
            InventoryMovement movement = new InventoryMovement();
            movement.setProduct(poLine.getProduct());
            movement.setWarehouse(po.getWarehouse());
            movement.setMovementType(MovementType.INBOUND.name());
            movement.setQuantity(qtyToReceive);
            movement.setPurchaseOrder(po);
            movement.setReferenceDoc(po.getPoNumber());
            movement.setNotes("Received from PO: " + po.getPoNumber());
            movement.setOccurredAt(LocalDateTime.now());
            inventoryMovementRepository.save(movement);
        }

        // Update PO status to RECEIVED
        po.setStatus(PurchaseOrderStatus.RECEIVED.name());
        po.setUpdatedAt(LocalDateTime.now());

        po = purchaseOrderRepository.save(po);
        return purchaseOrderMapper.toDTO(po);
    }

    // ========== 4. CANCEL PURCHASE ORDER ==========
    @Transactional
    public PurchaseOrderDTO cancelPurchaseOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Purchase Order not found"));

        // Validate status - can only cancel DRAFT or APPROVED
        if (PurchaseOrderStatus.RECEIVED.name().equals(po.getStatus())) {
            throw new BusinessException("Cannot cancel: Purchase Order is already RECEIVED");
        }

        if (PurchaseOrderStatus.CANCELED.name().equals(po.getStatus())) {
            throw new BusinessException("Purchase Order is already CANCELED");
        }

        po.setStatus(PurchaseOrderStatus.CANCELED.name());
        po.setUpdatedAt(LocalDateTime.now());

        po = purchaseOrderRepository.save(po);
        return purchaseOrderMapper.toDTO(po);
    }

    // ========== HELPER METHODS ==========

    public PurchaseOrderDTO getPurchaseOrderById(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Purchase Order not found"));
        return purchaseOrderMapper.toDTO(po);
    }

    public List<PurchaseOrderDTO> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream()
                .map(purchaseOrderMapper::toDTO)
                .toList();
    }

    public List<PurchaseOrderDTO> getPurchaseOrdersByStatus(String status) {
        return purchaseOrderRepository.findByStatus(status).stream()
                .map(purchaseOrderMapper::toDTO)
                .toList();
    }

    public List<PurchaseOrderDTO> getPurchaseOrdersByWarehouse(Long warehouseId) {
        return purchaseOrderRepository.findByWarehouseId(warehouseId).stream()
                .map(purchaseOrderMapper::toDTO)
                .toList();
    }

    private Inventory findOrCreateInventory(Product product, Warehouse warehouse) {
        // Try to find existing inventory
        Optional<Inventory> existingInventory = inventoryRepository.findAll().stream()
                .filter(inv -> inv.getProduct().getId().equals(product.getId())
                            && inv.getWarehouse().getId().equals(warehouse.getId()))
                .findFirst();

        if (existingInventory.isPresent()) {
            return existingInventory.get();
        }

        // Create new inventory if not exists
        Inventory newInventory = new Inventory();
        newInventory.setProduct(product);
        newInventory.setWarehouse(warehouse);
        newInventory.setQtyOnHand(0);
        newInventory.setQtyReserved(0);
        newInventory.setUpdatedAt(LocalDateTime.now());
        return inventoryRepository.save(newInventory);
    }

    private String generatePoNumber() {
        // Simple PO number generator: PO-TIMESTAMP
        return "PO-" + System.currentTimeMillis();
    }
}
