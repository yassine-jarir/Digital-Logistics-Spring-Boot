package com.logistic.digitale_logistic.service.warehouse_manager;

import com.logistic.digitale_logistic.dto   .PoLineDTO;
import com.logistic.digitale_logistic.dto.PurchaseOrderDTO;
import com.logistic.digitale_logistic.dto.ReceiveLineDTO;
import com.logistic.digitale_logistic.entity.*;
import com.logistic.digitale_logistic.enums.MovementType;
import com.logistic.digitale_logistic.enums.PurchaseOrderStatus;
import com.logistic.digitale_logistic.exceptions.BusinessException;
import com.logistic.digitale_logistic.mapper.PurchaseOrderMapper;
import com.logistic.digitale_logistic.repository.*;
import com.logistic.digitale_logistic.service.client.BackorderFulfillmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PoLineRepository poLineRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final SupplierRepository supplierRepository;
    private final WareHouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final BackorderFulfillmentService backorderFulfillmentService;

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
        po.setStatus(PurchaseOrderStatus.DRAFT);
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
        if (po.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new BusinessException("Cannot approve: Purchase Order is not in DRAFT status");
        }

        po.setStatus(PurchaseOrderStatus.APPROVED);
        po.setUpdatedAt(LocalDateTime.now());

        po = purchaseOrderRepository.save(po);
        return purchaseOrderMapper.toDTO(po);
    }

    /**
     * ========== 3B. RECEIVE ENTIRE PURCHASE ORDER (SIMPLIFIED) ==========
     * Automatically receives ALL lines with their full ordered quantities
     * Just pass the PO ID - no need to specify quantities per line
     */
    @Transactional
    public PurchaseOrderDTO receiveEntirePurchaseOrder(Long id) {
        log.info("Receiving ENTIRE Purchase Order ID: {}", id);

        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Purchase Order not found"));

        // Validate status
        if (po.getStatus() != PurchaseOrderStatus.APPROVED) {
            throw new BusinessException("Cannot receive: Purchase Order must be APPROVED first");
        }

        if (po.getLines().isEmpty()) {
            throw new BusinessException("Purchase Order has no lines to receive");
        }

        // Process ALL lines automatically with their full ordered quantity
        for (PoLine poLine : po.getLines()) {
            Integer qtyToReceive = poLine.getOrderedQuantity() - poLine.getReceivedQuantity();

            if (qtyToReceive <= 0) {
                log.warn("PO Line {} already fully received, skipping", poLine.getId());
                continue;
            }

            log.info("Receiving {} units of {} (PO Line ID: {})",
                    qtyToReceive, poLine.getProduct().getSku(), poLine.getId());

            // Update PO Line
            poLine.setReceivedQuantity(poLine.getReceivedQuantity() + qtyToReceive);
            poLine.setReceivedDate(LocalDateTime.now());

            // Update Inventory (qtyOnHand)
            Inventory inventory = findOrCreateInventory(poLine.getProduct(), po.getWarehouse());
            inventory.setQtyOnHand(inventory.getQtyOnHand() + qtyToReceive);
            inventory.setUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(inventory);

            log.info("Updated inventory for {} in {}: qtyOnHand = {}",
                    poLine.getProduct().getSku(), po.getWarehouse().getName(), inventory.getQtyOnHand());

            // Create Inventory Movement (INBOUND)
            InventoryMovement movement = new InventoryMovement();
            movement.setProduct(poLine.getProduct());
            movement.setWarehouse(po.getWarehouse());
            movement.setMovementType(MovementType.INBOUND);
            movement.setQuantity(qtyToReceive);
            movement.setPurchaseOrder(po);
            movement.setReferenceDoc(po.getPoNumber());
            movement.setNotes("Full receipt from PO: " + po.getPoNumber());
            movement.setOccurredAt(LocalDateTime.now());
            inventoryMovementRepository.save(movement);

            // Automatically allocate to pending backorders
            log.info("Checking for pending backorders for Product ID: {}, Warehouse ID: {}",
                    poLine.getProduct().getId(), po.getWarehouse().getId());

            try {
                backorderFulfillmentService.processPendingBackorders(
                        poLine.getProduct().getId(),
                        po.getWarehouse().getId(),
                        qtyToReceive,
                        po
                );
            } catch (Exception e) {
                log.error("Error processing backorders after receiving stock: {}", e.getMessage(), e);
                // Don't fail the entire receive operation if backorder processing fails
            }
        }

        // Update PO status to RECEIVED
        po.setStatus(PurchaseOrderStatus.RECEIVED);
        po.setUpdatedAt(LocalDateTime.now());

        po = purchaseOrderRepository.save(po);

        log.info("Purchase Order {} fully received successfully with {} lines", po.getPoNumber(), po.getLines().size());

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

    public List<PurchaseOrderDTO> getPurchaseOrdersByStatus(PurchaseOrderStatus status) {
        return purchaseOrderRepository.findByStatus(status).stream()
                .map(purchaseOrderMapper::toDTO)
                .toList();
    }



    private Inventory findOrCreateInventory(Product product, Warehouse warehouse) {
        // Try to find existing inventory using optimized query
        Optional<Inventory> existingInventory = inventoryRepository.findByProduct_IdAndWarehouse_Id(
                product.getId(),
                warehouse.getId()
        );

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
        return "PO-" + System.currentTimeMillis();
    }
}
