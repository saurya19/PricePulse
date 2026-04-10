package tatakae.pricepulse.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import tatakae.pricepulse.dto.ProductResponse;
import tatakae.pricepulse.model.Product;
import tatakae.pricepulse.service.ProductService;

@Tag(name = "Product", description = "Product management endpoints")
@RestController
@RequestMapping("/products")
public class ProductController {
	
	 
	 private final ProductService productService;
	 public ProductController(ProductService productService) {
		 this.productService = productService;
	 }

	 @Operation(summary = "Add a new product")
	 @PostMapping
	 public ProductResponse addProduct(@Valid @RequestBody Product product) {

	     Product savedProduct = productService.addProduct(product);

	     return productService.convertToProductResponse(savedProduct);
	 }

	 @Operation(summary = "Get all products paginated")
	 @GetMapping
	 public Page<ProductResponse> getAllProduct(
	         @RequestParam(defaultValue = "0") int page,
	         @RequestParam(defaultValue = "5") int size) {

	     Page<Product> productPage = productService.getAllProduct(page, size);

	     return productPage.map(product ->
	             productService.convertToProductResponse(product));
	 }

	 @Operation(summary = "Get products by category")
	 @GetMapping("/category/{category}")
	 public List<ProductResponse> getByCategory(@PathVariable String category) {

	     List<Product> products = productService.getByCategory(category);

	     return products.stream()
	             .map(product -> productService.convertToProductResponse(product))
	             .toList();
	 }

}
