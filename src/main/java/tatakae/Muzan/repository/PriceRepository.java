package tatakae.Muzan.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tatakae.Muzan.Model.Price;
import tatakae.Muzan.Model.Product;

public interface PriceRepository extends JpaRepository<Price, Integer>{

	@EntityGraph(attributePaths= {"product"})
	Page<Price> findByProductOrderByDateDesc(Product product, Pageable pageable);
	@EntityGraph(attributePaths= {"product"})
	Price findTopByProductOrderByDateDesc(Product product);
	@EntityGraph(attributePaths= {"product"})
	Price findTopByProductOrderByPriceAsc(Product product);
	@Query("SELECT MIN(p.price) FROM Price p WHERE p.product = :product")
	BigDecimal findLowestPriceByProduct(@Param("product") Product product);
	@Query("SELECT AVG(p.price) FROM Price p WHERE p.product = :product")
	BigDecimal findAveragePriceByProduct(@Param("product") Product product);
}