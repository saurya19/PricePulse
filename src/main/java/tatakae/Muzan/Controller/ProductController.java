package tatakae.Muzan.Controller;

import java.util.List;
import org.springframework.data.domain.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;
import tatakae.Muzan.DTO.ProductResponse;
import tatakae.Muzan.Model.Product;
import tatakae.Muzan.Service.ProductService;
import tatakae.Muzan.repository.ProductRepository;

@RestController
@RequestMapping("/products")
public class ProductController {
	
	 
	 private final ProductService productService;
	 public ProductController(ProductService productService) {
		 this.productService = productService;
	 }

	 // POST /products
	 @PostMapping
	 public ProductResponse addProduct(@Valid @RequestBody Product product) {

	     Product savedProduct = productService.addProduct(product);

	     return productService.convertToProductResponse(savedProduct);
	 }

	 // GET /products?page=0&size=5
	 @GetMapping
	 public Page<ProductResponse> getAllProduct(
	         @RequestParam(defaultValue = "0") int page,
	         @RequestParam(defaultValue = "5") int size) {

	     Page<Product> productPage = productService.getAllProduct(page, size);

	     return productPage.map(product ->
	             productService.convertToProductResponse(product));
	 }

	 // GET /products/category/{category}
	 @GetMapping("/category/{category}")
	 public List<ProductResponse> getByCategory(@PathVariable String category) {

	     List<Product> products = productService.getByCategory(category);

	     return products.stream()
	             .map(product -> productService.convertToProductResponse(product))
	             .toList();
	 }

}
