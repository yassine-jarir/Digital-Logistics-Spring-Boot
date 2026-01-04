package com.logistic.digitale_logistic.service.client;

import com.logistic.digitale_logistic.config.SecurityUtils;
import com.logistic.digitale_logistic.dto.ReservationResultDTO;
import com.logistic.digitale_logistic.dto.SalesOrderDTO;
import com.logistic.digitale_logistic.dto.SalesOrderWithReservationDTO;
import com.logistic.digitale_logistic.dto.SoLineDTO;
import com.logistic.digitale_logistic.entity.*;
import com.logistic.digitale_logistic.mapper.SalesOrderMapper;
import com.logistic.digitale_logistic.repository.ClientRepository;
import com.logistic.digitale_logistic.repository.ProductRepository;
import com.logistic.digitale_logistic.repository.SalesOrderRepository;
import com.logistic.digitale_logistic.repository.WareHouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final WareHouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final SalesOrderMapper salesOrderMapper;
    private final InventoryReservationService inventoryReservationService;
    private final ClientRepository clientRepository;

    @Transactional
    public SalesOrderWithReservationDTO createSalesOrder(SalesOrderDTO dto) {

        String ownerSub = SecurityUtils.currentUserSub();

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setOrderNumber(generateOrderNumber());
        salesOrder.setOwnerSub(ownerSub);
        salesOrder.setWarehouse(warehouse);
        salesOrder.setClient(client);
        salesOrder.setStatus("CREATED");
        salesOrder.setOrderDate(LocalDateTime.now());
        salesOrder.setUpdatedAt(LocalDateTime.now());

        List<SoLine> lines = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SoLineDTO lineDto : dto.getLines()) {

            Product product = productRepository.findById(lineDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            if (!product.getActive()) {
                throw new IllegalArgumentException("Product is not active");
            }

            if (lineDto.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }

            BigDecimal unitPrice = product.getSellingPrice();
            if (unitPrice == null) {
                throw new IllegalArgumentException("Product has no selling price");
            }

            SoLine line = new SoLine();
            line.setSalesOrder(salesOrder);
            line.setProduct(product);
            line.setOrderedQuantity(lineDto.getQuantity());
            line.setReservedQuantity(0);
            line.setUnitPrice(unitPrice);

            lines.add(line);
            totalAmount = totalAmount.add(unitPrice.multiply(BigDecimal.valueOf(lineDto.getQuantity())));
        }

        salesOrder.setLines(lines);
        salesOrder.setTotalAmount(totalAmount);

        SalesOrder savedOrder = salesOrderRepository.save(salesOrder);

        ReservationResultDTO reservationResult;
        try {
            reservationResult = inventoryReservationService.processOrderReservation(savedOrder.getId());
            savedOrder = salesOrderRepository.findById(savedOrder.getId()).orElse(savedOrder);
        } catch (Exception e) {
            reservationResult = ReservationResultDTO.builder()
                    .salesOrderId(savedOrder.getId())
                    .salesOrderNumber(savedOrder.getOrderNumber())
                    .status("CREATED")
                    .fullyReserved(false)
                    .hasBackorders(false)
                    .message("Reservation failed: " + e.getMessage())
                    .build();
        }

        return SalesOrderWithReservationDTO.builder()
                .salesOrder(salesOrderMapper.toDTO(savedOrder))
                .reservationResult(reservationResult)
                .build();
    }

    @Transactional(readOnly = true)
    public List<SalesOrderDTO> getMyOrders() {
        return salesOrderRepository.findByOwnerSub(SecurityUtils.currentUserSub())
                .stream()
                .map(salesOrderMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SalesOrderDTO> getAllSalesOrders() {
        return salesOrderRepository.findAll()
                .stream()
                .map(salesOrderMapper::toDTO)
                .toList();
    }

    private String generateOrderNumber() {
        return "SO-" + System.currentTimeMillis();
    }
}
