package com.logistic.digitale_logistic.service.Admin;

import com.logistic.digitale_logistic.dto.ProductDTO;
import com.logistic.digitale_logistic.dto.UserDTO;
import com.logistic.digitale_logistic.entity.Product;
import com.logistic.digitale_logistic.entity.SalesOrder;
import com.logistic.digitale_logistic.entity.SoLine;
import com.logistic.digitale_logistic.mapper.ProductMapper;
import com.logistic.digitale_logistic.repository.InventoryRepository;
import com.logistic.digitale_logistic.repository.ProductRepository;
import com.logistic.digitale_logistic.repository.SalesOrderRepository;
import com.logistic.digitale_logistic.repository.SoLineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final SalesOrderRepository salesOrderRepository;
    private final InventoryRepository inventoryRepository;
    private final SoLineRepository soLineRepository;

    public ProductService(ProductRepository repo, ProductMapper mapper, SalesOrderRepository salesOrderRepository, InventoryRepository inventoryRepository, SoLineRepository soLineRepository) {
        this.productRepository = repo;
        this.productMapper = mapper;
        this.salesOrderRepository = salesOrderRepository;
        this.inventoryRepository = inventoryRepository;
        this.soLineRepository = soLineRepository;
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toProductDTO)
                .toList();
    }

    public ProductDTO createProduct(ProductDTO dto) {
        Product product = productMapper.toProductEntity(dto);

        if (product.getSku() == null || product.getSku().isEmpty()) {
            product.setSku("SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        Product saved = productRepository.save(product);
        return productMapper.toProductDTO(saved);
    }

    public ProductDTO updateProduct(ProductDTO dto, Long id) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new Exception("Product not found"));

        // Partial update
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getCategory() != null) product.setCategory(dto.getCategory());
        if (dto.getCostPrice() != null) product.setCostPrice(dto.getCostPrice());
        if (dto.getSellingPrice() != null) product.setSellingPrice(dto.getSellingPrice());
        if (dto.getActive() != null) product.setActive(dto.getActive());

        Product updated = productRepository.save(product);
        return productMapper.toProductDTO(updated);
    }

    public void activateProduct(Long id) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new Exception("Product not found"));
        product.setActive(true);
        productRepository.save(product);
    }

    public void deactivateProduct(String sku) throws Exception {
        Product product = productRepository.findBySku(sku);

        if (product == null) {
            throw new Exception("Product not found");
        }
        Product prod = productRepository.findBySku(sku);

        if (prod == null) {
            throw new Exception("Product with SKU " + sku + " not found.");
        }

        List<SoLine> soLine = soLineRepository.findProductById(prod.getId());

        if(!soLine.isEmpty()){
            for(SoLine so: soLine){
                SalesOrder salesOrder = salesOrderRepository.findById(so.getId()).orElseThrow(() -> new Exception("Sales Order not found"));
                String status = salesOrder.getStatus();
                    if(status.equals("CREATED") || status.equals("RESERVED")){
                    throw new Exception("cannot desactivate product linked to active sales order ");
                }
            }
        }

        Integer qtyReserved = inventoryRepository.findTotalQtyReservedByProductId(prod.getId());
        if(qtyReserved > 0){
            throw new Exception("cannot desactivate product qty > 0 ");
        }
        product.setActive(false);
        productRepository.save(product);
    }
}

