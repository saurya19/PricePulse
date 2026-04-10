package tatakae.pricepulse.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import tatakae.pricepulse.dto.ProductResponse;
import tatakae.pricepulse.model.Price;
import tatakae.pricepulse.model.Product;
import tatakae.pricepulse.repository.ProductRepository;

@Service
public class ProductService {

	
	private final ProductRepository productRepo;
	public ProductService(ProductRepository productRepo) {
		this.productRepo = productRepo;
	}
	
	
	public Product addProduct(Product product) {
		
		return productRepo.save(product);
	}
	
	public Page<Product> getAllProduct(int page, int size){
		
		Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
		return productRepo.findAll(pageable);
		
	}
	
	public List<Product> getByCategory(String category){
		
		return productRepo.findByCategory(category);
		
	}
	
	public ProductResponse convertToProductResponse(Product product) {
		return new ProductResponse(product.getId(),
					product.getName(),
					product.getCategory(),
					product.getRating(),
					product.getImageURL(),
					product.getDescription()
					);
	}
	
}
