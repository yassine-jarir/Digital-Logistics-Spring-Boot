package com.logistic.digitale_logistic.service.Admin;

import com.logistic.digitale_logistic.dto.ProductDTO;
import com.logistic.digitale_logistic.dto.UserDTO;
import com.logistic.digitale_logistic.entity.Product;
import com.logistic.digitale_logistic.mapper.ProductMapper;
import com.logistic.digitale_logistic.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository repo, ProductMapper mapper) {
        this.productRepository = repo;
        this.productMapper = mapper;
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

    public void deactivateProduct(Long id) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new Exception("Product not found"));
        product.setActive(false);
        productRepository.save(product);
    }
}
