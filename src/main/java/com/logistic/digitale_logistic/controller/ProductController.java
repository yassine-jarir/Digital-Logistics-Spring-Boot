package com.logistic.digitale_logistic.controller;

import com.logistic.digitale_logistic.dto.ProductDTO;
import com.logistic.digitale_logistic.dto.UserDTO;
import com.logistic.digitale_logistic.entity.Product;
import com.logistic.digitale_logistic.service.Admin.ProductService;
import com.logistic.digitale_logistic.service.Admin.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Table;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
@RequestMapping("/api/products")
@Tag(name = "Admin - Products", description = "Admin endpoints for managing products")
@SecurityRequirement(name = "Bearer Authentication")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService service, UserService userService) {
        this.productService = service;
    }

    @Operation(
            summary = "Get all products",
            description = "Retrieve all products in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all products"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductDTO>> findAll() {
        List<ProductDTO>  productDTO = productService.getAllProducts();
        return ResponseEntity.ok(productDTO);

    }

    @Operation(
            summary = "Create new product",
            description = "Create a new product in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
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

    @Operation(
            summary = "Update product",
            description = "Update an existing product by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(
            @RequestBody ProductDTO product,
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long id) {
        try {
            ProductDTO updated = productService.updateProduct(product, id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Operation(
            summary = "Deactivate product by SKU",
            description = "Deactivate a product using its SKU code"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deactivated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid SKU or product not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("{sku}/deactivate")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deactivateProduct(
            @Parameter(description = "Product SKU", required = true, example = "SKU-12345")
            @PathVariable String sku) {
        try {
            productService.deactivateProduct(sku);
            return ResponseEntity.ok("{\"message\":\"Product with SKU " + sku + " has been deactivated.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }


}
