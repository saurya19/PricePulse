package tatakae.Muzan.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tatakae.Muzan.DTO.PriceAlertRequest;
import tatakae.Muzan.Exception.ProductNotFoundException;
import tatakae.Muzan.Model.Price;
import tatakae.Muzan.Model.PriceAlert;
import tatakae.Muzan.Model.Product;
import tatakae.Muzan.repository.PriceAlertRepository;
import tatakae.Muzan.repository.PriceRepository;
import tatakae.Muzan.repository.ProductRepository;

@Service
public class PriceAlertService {
	
	private final PriceAlertRepository alertRepo;
	private final PriceRepository priceRepo;
	private final ProductRepository productRepo;
	private final EmailService emailService;
	
	private static final Logger log = LoggerFactory.getLogger(PriceAlertService.class);
	
	public PriceAlertService(PriceAlertRepository alertRepo,
            ProductRepository productRepo,
            PriceRepository priceRepo,
            EmailService emailService) {
				this.alertRepo = alertRepo;
				this.productRepo = productRepo;
				this.priceRepo = priceRepo;
				this.emailService = emailService;
	}
	
	public PriceAlert createPriceAlert(PriceAlertRequest request) {
		
		Product product = productRepo.findById(request.getProductId()).orElseThrow(()-> new ProductNotFoundException(request.getProductId()));
		
		PriceAlert priceAlert = new PriceAlert();
		priceAlert.setEmail(request.getEmail());
		priceAlert.setProduct(product);
		priceAlert.setTargetPrice(request.getTargetPrice());
		priceAlert.setTriggered(false);
		priceAlert.setCreatedAt(LocalDateTime.now());
		
		log.info("Alert created for {} on product {} with target {}", request.getEmail(), product.getName(), request.getTargetPrice());
		return alertRepo.save(priceAlert);
	}
	
	public void checkAndTriggerAlerts() {
        List<PriceAlert> activeAlerts = alertRepo.findByTriggeredFalse();

        log.info("Checking {} active alerts", activeAlerts.size());

        for (PriceAlert alert : activeAlerts) {
            try {
                Price latestPrice = priceRepo.findTopByProductOrderByDateDesc(alert.getProduct());

                if (latestPrice == null) continue;

                BigDecimal currentPrice = latestPrice.getPrice();
                BigDecimal targetPrice = alert.getTargetPrice();

                if (currentPrice.compareTo(targetPrice) <= 0) {
                    log.info("Alert triggered for {} — current price {} <= target {}",
                            alert.getEmail(), currentPrice, targetPrice);

                    emailService.sendPriceAlert(alert,
                            currentPrice.toString(),
                            latestPrice.getWebsite());

                    alert.setTriggered(true);
                    alertRepo.save(alert);
                }

            } catch (Exception e) {
                log.error("Error checking alert ID {}: {}", alert.getId(), e.getMessage());
            }
        }
    }
	
	public List<PriceAlert> getAllAlerts(){
		return alertRepo.findAll();
	}

}
