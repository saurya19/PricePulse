package tatakae.pricepulse.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tatakae.pricepulse.model.PriceAlert;
import tatakae.pricepulse.model.Product;

public interface PriceAlertRepository extends JpaRepository<PriceAlert, Integer>{
	List<PriceAlert> findByTriggeredFalse();
	List<PriceAlert> findByProductAndTriggeredFalse(Product product);
}
