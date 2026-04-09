package tatakae.Muzan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tatakae.Muzan.Model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer>{

	List<Product> findByCategory(String category);
	boolean existsByUrl(String url);
	
}
