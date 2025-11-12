package com.logistic.digitale_logistic.service.client;

import com.logistic.digitale_logistic.dto.ReservationResultDTO;
import com.logistic.digitale_logistic.dto.SalesOrderDTO;
import com.logistic.digitale_logistic.dto.SalesOrderWithReservationDTO;
import com.logistic.digitale_logistic.dto.SoLineDTO;
import com.logistic.digitale_logistic.entity.*;
import com.logistic.digitale_logistic.mapper.SalesOrderMapper;
import com.logistic.digitale_logistic.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final ClientRepository clientRepository;
    private final WareHouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final SalesOrderMapper salesOrderMapper;
    private final InventoryReservationService inventoryReservationService;

    @Transactional
    public SalesOrderWithReservationDTO createSalesOrder(SalesOrderDTO dto) {
        log.info("Creating sales order for client ID: {}, warehouse ID: {}", dto.getClientId(), dto.getWarehouseId());

        // Validate client exists
        com.logistic.digitale_logistic.entity.Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + dto.getClientId()));

        // Validate warehouse exists
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found with ID: " + dto.getWarehouseId()));

        // Create sales order
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setOrderNumber(generateOrderNumber());
        salesOrder.setClient(client);
        salesOrder.setWarehouse(warehouse);
        salesOrder.setStatus("CREATED");
        salesOrder.setOrderDate(LocalDateTime.now());
        salesOrder.setUpdatedAt(LocalDateTime.now());
        salesOrder.setTotalAmount(BigDecimal.ZERO);

        // Create and validate lines
        List<SoLine> lines = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SoLineDTO lineDto : dto.getLines()) {
            Product product = productRepository.findById(lineDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + lineDto.getProductId()));

            if (!product.getActive()) {
                throw new IllegalArgumentException("Product is not active: " + product.getName());
            }

            if (lineDto.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }

            BigDecimal unitPrice = product.getSellingPrice();
            if (unitPrice == null) {
                throw new IllegalArgumentException("Product does not have a selling price: " + product.getName());
            }

            SoLine line = new SoLine();
            line.setSalesOrder(salesOrder);
            line.setProduct(product);
            line.setOrderedQuantity(lineDto.getQuantity());
            line.setReservedQuantity(0);
            line.setUnitPrice(unitPrice);

            lines.add(line);

            BigDecimal lineTotal = unitPrice.multiply(new BigDecimal(lineDto.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);
        }

        salesOrder.setLines(lines);
        salesOrder.setTotalAmount(totalAmount);

        // Save
        SalesOrder savedOrder = salesOrderRepository.save(salesOrder);

        log.info("Sales order created: {} with {} lines", savedOrder.getOrderNumber(), savedOrder.getLines().size());

        // 💥 AUTOMATIC RESERVATION - Happens immediately after order creation!
        ReservationResultDTO reservationResult = null;
        try {
            log.info("🚀 Triggering automatic reservation for order: {}", savedOrder.getOrderNumber());
            reservationResult = inventoryReservationService.processOrderReservation(savedOrder.getId());

            log.info("✅ Automatic reservation completed - Status: {}, Fully Reserved: {}, Backorders: {}",
                    reservationResult.getStatus(),
                    reservationResult.isFullyReserved(),
                    reservationResult.isHasBackorders());

            // Refresh the order to get updated status and reserved quantities
            savedOrder = salesOrderRepository.findById(savedOrder.getId()).orElse(savedOrder);

        } catch (Exception e) {
            log.error("❌ Error during automatic reservation for order {}: {}", savedOrder.getOrderNumber(), e.getMessage(), e);
            // Create error reservation result
            reservationResult = ReservationResultDTO.builder()
                    .salesOrderId(savedOrder.getId())
                    .salesOrderNumber(savedOrder.getOrderNumber())
                    .status("CREATED")
                    .fullyReserved(false)
                    .hasBackorders(false)
                    .message("⚠️ Reservation failed: " + e.getMessage() + ". Please retry manually or contact support.")
                    .build();
        }

        // Return combined response
        return SalesOrderWithReservationDTO.builder()
                .salesOrder(salesOrderMapper.toDTO(savedOrder))
                .reservationResult(reservationResult)
                .build();
    }

    @Transactional(readOnly = true)
    public List<SalesOrderDTO> getAllSalesOrders() {
        return salesOrderRepository.findAll().stream()
                .map(salesOrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SalesOrderDTO> getClientOrders(Long clientId) {
        return salesOrderRepository.findByClientUserId(clientId).stream()
                .map(salesOrderMapper::toDTO)
                .collect(Collectors.toList());
    }


    private String generateOrderNumber() {
        return "SO-" + System.currentTimeMillis();
    }
}
