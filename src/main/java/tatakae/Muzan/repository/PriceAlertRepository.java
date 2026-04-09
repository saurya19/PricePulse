package tatakae.Muzan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tatakae.Muzan.Model.PriceAlert;
import tatakae.Muzan.Model.Product;

public interface PriceAlertRepository extends JpaRepository<PriceAlert, Integer>{
	List<PriceAlert> findByTriggeredFalse();
	List<PriceAlert> findByProductAndTriggeredFalse(Product product);
}
