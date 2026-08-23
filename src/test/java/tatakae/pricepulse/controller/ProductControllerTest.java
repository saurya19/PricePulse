package tatakae.pricepulse.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tatakae.pricepulse.dto.ProductResponse;
import tatakae.pricepulse.model.Product;
import tatakae.pricepulse.security.JwtAuthFilter;
import tatakae.pricepulse.service.ProductService;

@WebMvcTest(
        controllers = ProductController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    void addProduct_returnsCreatedProduct() throws Exception {
        Product saved = new Product();
        saved.setId(1);
        saved.setName("Clean Code");
        saved.setCategory("Programming");

        ProductResponse response = new ProductResponse(1, "Clean Code", "Programming", 5, "img.jpg", "desc");

        when(productService.addProduct(any(Product.class))).thenReturn(saved);
        when(productService.convertToProductResponse(saved)).thenReturn(response);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Clean Code",
                                  "category": "Programming",
                                  "url": "https://example.com/clean-code",
                                  "imageURL": "img.jpg",
                                  "description": "desc",
                                  "rating": 5
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Clean Code"))
                .andExpect(jsonPath("$.category").value("Programming"));
    }

    @Test
    void getAllProduct_returnsPaginatedResults() throws Exception {
        Product product = new Product();
        product.setId(1);
        product.setName("Atomic Habits");

        ProductResponse response = new ProductResponse(1, "Atomic Habits", "Self-help", 4, "img.jpg", "desc");

        when(productService.getAllProduct(0, 5))
                .thenReturn(new PageImpl<>(List.of(product), PageRequest.of(0, 5), 1));
        when(productService.convertToProductResponse(product)).thenReturn(response);

        mockMvc.perform(get("/products")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Atomic Habits"));
    }

    @Test
    void getByCategory_returnsMatchingProducts() throws Exception {
        Product product = new Product();
        product.setId(2);
        product.setName("The Pragmatic Programmer");
        product.setCategory("Programming");

        ProductResponse response = new ProductResponse(2, "The Pragmatic Programmer", "Programming", 5, "img.jpg", "desc");

        when(productService.getByCategory("Programming")).thenReturn(List.of(product));
        when(productService.convertToProductResponse(product)).thenReturn(response);

        mockMvc.perform(get("/products/category/Programming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("The Pragmatic Programmer"))
                .andExpect(jsonPath("$[0].category").value("Programming"));
    }
}