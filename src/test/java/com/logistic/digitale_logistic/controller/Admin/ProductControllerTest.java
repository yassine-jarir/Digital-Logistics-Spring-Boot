//package com.logistic.digitale_logistic.controller.Admin;
//
//import com.logistic.digitale_logistic.controller.ProductController;
//import com.logistic.digitale_logistic.dto.ProductDTO;
//import com.logistic.digitale_logistic.service.Admin.ProductService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import org.springframework.http.ResponseEntity;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class ProductControllerTest {
//
//    @Mock
//    private ProductService productService;
//
//    @InjectMocks
//    private ProductController controller;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // ===================== TEST 1: findAll() =====================
//    @Test
//    void testFindAll() {
//        ProductDTO dto = new ProductDTO();
//        dto.setId(1L);
//
//        when(productService.getAllProducts()).thenReturn(List.of(dto));
//
//        ResponseEntity<List<ProductDTO>> response = controller.findAll();
//
//        assertEquals(1, response.getBody().size());
//        assertEquals(1L, response.getBody().get(0).getId());
//    }
//
//    // ===================== TEST 2: create() success =====================
//    @Test
//    void testCreateProduct_Success()  {
//        ProductDTO input = new ProductDTO();
//        input.setName("Laptop");
//
//        ProductDTO returned = new ProductDTO();
//        returned.setName("Laptop");
//        returned.setId(10L);
//
//        when(productService.createProduct(input)).thenReturn(returned);
//
//        ResponseEntity<?> response = controller.create(input);
//
//        assertEquals(10L, ((ProductDTO) response.getBody()).getId());
//    }
//
//    // ===================== TEST 3: create() error =====================
//    @Test
//    void testCreateProduct_Error()  {
//        ProductDTO input = new ProductDTO();
//
//        when(productService.createProduct(input))
//                .thenThrow(new RuntimeException("Invalid product"));
//
//        ResponseEntity<?> response = controller.create(input);
//
//        assertTrue(response.getBody().toString().contains("Invalid product"));
//    }
//
//    // ===================== TEST 4: update() success =====================
//    @Test
//    void testUpdateProduct_Success() throws Exception {
//        ProductDTO input = new ProductDTO();
//        ProductDTO updated = new ProductDTO();
//        updated.setId(5L);
//
//        when(productService.updateProduct(input, 5L)).thenReturn(updated);
//
//        ResponseEntity<?> response = controller.update(input, 5L);
//
//        assertEquals(5L, ((ProductDTO) response.getBody()).getId());
//    }
//
//    // ===================== TEST 5: update() error =====================
//    @Test
//    void testUpdateProduct_Error() throws Exception {
//        ProductDTO input = new ProductDTO();
//
//        when(productService.updateProduct(input, 5L))
//                .thenThrow(new RuntimeException("Not found"));
//
//        ResponseEntity<?> response = controller.update(input, 5L);
//
//        assertTrue(response.getBody().toString().contains("Not found"));
//    }
//
//    // ===================== TEST 6: deactivateProduct() success =====================
//    @Test
//    void testDeactivateProduct_Success() throws Exception {
//        ResponseEntity<?> response = controller.deactivateProduct("SKU123");
//
//        assertTrue(response.getBody().toString().contains("SKU123"));
//
//        verify(productService, times(1)).deactivateProduct("SKU123");
//    }
//
//    // ===================== TEST 7: deactivateProduct() error =====================
//    @Test
//    void testDeactivateProduct_Error() throws Exception {
//        doThrow(new RuntimeException("SKU invalid"))
//                .when(productService).deactivateProduct("BADSKU");
//
//        ResponseEntity<?> response = controller.deactivateProduct("BADSKU");
//
//        assertTrue(response.getBody().toString().contains("SKU invalid"));
//    }
//}
