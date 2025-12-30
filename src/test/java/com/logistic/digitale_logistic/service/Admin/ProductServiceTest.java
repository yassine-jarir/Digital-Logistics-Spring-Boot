//package com.logistic.digitale_logistic.service.Admin;
//
//import com.logistic.digitale_logistic.dto.ProductDTO;
//import com.logistic.digitale_logistic.entity.Product;
//import com.logistic.digitale_logistic.mapper.ProductMapper;
//import com.logistic.digitale_logistic.repository.ProductRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ProductServiceTest {
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private ProductMapper productMapper;
//
//    @InjectMocks
//    private ProductService productService;
//
//    @Test
//    void testCreateProduct_GeneratesSkuIfMissing() {
//        // Given
//        ProductDTO dto = new ProductDTO();
//        dto.setName("Laptop");
//
//        Product product = new Product();
//        product.setName("Laptop");
//
//        when(productMapper.toProductEntity(dto)).thenReturn(product);
//        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(productMapper.toProductDTO(any(Product.class))).thenReturn(new ProductDTO());
//
//        // When
//        productService.createProduct(dto);
//
//        // Then
//        verify(productRepository).save(argThat(p -> {
//            assertNotNull(p.getSku());
//            assertTrue(p.getSku().startsWith("SKU-"));
//            assertEquals(12, p.getSku().length());
//            return true;
//        }));
//    }
//}
