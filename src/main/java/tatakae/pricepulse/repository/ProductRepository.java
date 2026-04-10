package tatakae.pricepulse.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tatakae.pricepulse.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer>{

	List<Product> findByCategory(String category);
	boolean existsByUrl(String url);
	
}
