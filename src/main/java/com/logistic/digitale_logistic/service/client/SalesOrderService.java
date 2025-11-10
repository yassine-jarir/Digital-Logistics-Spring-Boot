package com.logistic.digitale_logistic.service.client;

import com.logistic.digitale_logistic.dto.SalesOrderDTO;
import com.logistic.digitale_logistic.dto.SoLineDTO;
import com.logistic.digitale_logistic.entity.*;
import com.logistic.digitale_logistic.mapper.SalesOrderMapper;
import com.logistic.digitale_logistic.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final ClientRepository clientRepository;
    private final WareHouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final SalesOrderMapper salesOrderMapper;

    @Transactional
    public SalesOrderDTO createSalesOrder(SalesOrderDTO dto) {
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
            // Validate product exists and is active
            Product product = productRepository.findById(lineDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + lineDto.getProductId()));

            if (!product.getActive()) {
                throw new IllegalArgumentException("Product is not active: " + product.getName());
            }

            // Validate quantity
            if (lineDto.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }

            // Create line
            SoLine line = new SoLine();
            line.setSalesOrder(salesOrder);
            line.setProduct(product);
            line.setOrderedQuantity(lineDto.getQuantity());
            line.setReservedQuantity(0); // No reservation yet
            line.setUnitPrice(lineDto.getUnitPrice());

            lines.add(line);

            // Calculate total (line_total is calculated by DB, but we calculate here for totalAmount)
            BigDecimal lineTotal = lineDto.getUnitPrice().multiply(new BigDecimal(lineDto.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);
        }

        salesOrder.setLines(lines);
        salesOrder.setTotalAmount(totalAmount);

        // Save
        SalesOrder savedOrder = salesOrderRepository.save(salesOrder);

        return salesOrderMapper.toDTO(savedOrder);
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

    @Transactional(readOnly = true)
    public SalesOrderDTO getSalesOrderById(Long id) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sales order not found with ID: " + id));
        return salesOrderMapper.toDTO(salesOrder);
    }

    private String generateOrderNumber() {
        return "SO-" + System.currentTimeMillis();
    }
}
