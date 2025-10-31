package com.logistic.digitale_logistic.controller.Admin;

import com.logistic.digitale_logistic.dto.ProductDTO;
import com.logistic.digitale_logistic.dto.UserDTO;
import com.logistic.digitale_logistic.entity.Product;
import com.logistic.digitale_logistic.service.Admin.ProductService;
import com.logistic.digitale_logistic.service.Admin.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final UserService userService;

    public ProductController(ProductService service, UserService userService) {
        this.productService = service;
        this.userService = userService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductDTO>> findAll() {
        List<ProductDTO>  productDTO = productService.getAllProducts();
        return ResponseEntity.ok(productDTO);

    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody ProductDTO product) {
        try {
            ProductDTO created = productService.createProduct(product);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@RequestBody ProductDTO product, @PathVariable Long id) {
        try {
            ProductDTO updated = productService.updateProduct(product, id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }


}
