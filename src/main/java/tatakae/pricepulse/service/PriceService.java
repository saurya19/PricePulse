package tatakae.pricepulse.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import tatakae.pricepulse.dto.BuySuggestionResponse;
import tatakae.pricepulse.dto.PriceRequest;
import tatakae.pricepulse.dto.PriceResponse;
import tatakae.pricepulse.exception.ProductNotFoundException;
import tatakae.pricepulse.model.Price;
import tatakae.pricepulse.model.Product;
import tatakae.pricepulse.repository.PriceRepository;
import tatakae.pricepulse.repository.ProductRepository;
import tatakae.pricepulse.scraper.PriceScraper;

@Service
public class PriceService {
	
	private final ProductRepository productRepo; 
	private final PriceRepository priceRepo;
	private final List<PriceScraper> scrapers;
	private final PriceAlertService alertService;

	public PriceService(ProductRepository productRepo, PriceRepository priceRepo, List<PriceScraper> scrapers, @Lazy PriceAlertService alertService) {
	    this.productRepo = productRepo;
	    this.priceRepo = priceRepo;
	    this.scrapers = scrapers;
	    this.alertService = alertService;
	}
	
	private static final Logger log = LoggerFactory.getLogger(PriceService.class);

	@CacheEvict(value = {"cheapestPrice", "latestPrice"}, key = "#productId")
	public Price addPrice(int productId, String website, BigDecimal priceVal) {
		Product product = productRepo.findById(productId)
			    .orElseThrow(() -> new ProductNotFoundException(productId));
		log.info("Saving Price for productId {} from website {} whose price is {}", productId, website, priceVal);
		Price price = new Price();
		price.setWebsite(website);
		price.setPrice(priceVal);
		price.setDate(LocalDateTime.now());
		price.setProduct(product);
		return priceRepo.save(price);
	}
	
	@Cacheable(value = "cheapestPrice", key = "#productId")
	public Price getCheapestPrice(int productId) {
		Product product = productRepo.findById(productId)
			    .orElseThrow(() -> new ProductNotFoundException(productId));
		log.info("Cache MISS — fetching cheapest price from DB for product " + productId);
		return priceRepo.findTopByProductOrderByPriceAsc(product);
	}
	
	@Cacheable(value = "latestPrice", key = "#productId")
	public Price getLatestPrice(int productId) {
		Product product = productRepo.findById(productId)
			    .orElseThrow(() -> new ProductNotFoundException(productId));
		log.info("Cache MISS — fetching latest price from DB for product " + productId);
		return priceRepo.findTopByProductOrderByDateDesc(product);
	}
	
	public Page<Price> getPrice(int productId, int page, int size){
		Product product = productRepo.findById(productId)
			    .orElseThrow(() -> new ProductNotFoundException(productId));
		Pageable pageable = PageRequest.of(page, size);
		return priceRepo.findByProductOrderByDateDesc(product, pageable);
	}
	
	public void addScraperPrice(int productId) {
		Product product = productRepo.findById(productId)
			    .orElseThrow(() -> new ProductNotFoundException(productId));
	    
	    log.info("Started Scraping for Product Id {}", productId);

	    for (PriceScraper scraper : scrapers) {
	    	try {
	    		log.info("Scraping started for website {}", scraper.getWebsiteName());
	    		BigDecimal fetchedPrice = scraper.fetchPrice(product.getUrl());
		        addPrice(productId, scraper.getWebsiteName(), fetchedPrice);
		        log.info("Successfully Scraped price for website {}", scraper.getWebsiteName());
	    	}
	    	catch(Exception e) {
	    		log.error("Scraping failed for website: {} productID: {}", scraper.getWebsiteName(), productId, e);
	    	}	        
	    }
	    log.info("Completed scraping for product ID: {}", productId);
	}

	// ✅ Extracted shared method — used by both scheduler and startup
	public void autoScrapeAllProducts() {
		List<Product> products = productRepo.findAll(); // removed 20-product limit
		log.info("Scrape started at {} for {} products", LocalDateTime.now(), products.size());

		for (Product product : products) {
			try {
				addScraperPrice(product.getId());
				log.info("Price updated for product ID: {}", product.getId());
			} catch (Exception e) {
				log.error("Failed to update product ID: {}", product.getId(), e);
			}
		}

		log.info("Scrape cycle completed at {}", LocalDateTime.now());
		alertService.checkAndTriggerAlerts();
	}

	// ✅ Hourly scheduler — now calls shared method
	@Scheduled(fixedRateString = "${price.scrape.interval}")
	public void autoScrapePrice() {
		log.info("Scheduler triggered at {}", LocalDateTime.now());
		autoScrapeAllProducts();
	}

	// ✅ Runs once on startup after app is fully ready (products already imported)
	@EventListener(ApplicationReadyEvent.class)
	public void scrapeOnStartup() {
		log.info("Startup price scrape triggered after app ready");
		autoScrapeAllProducts();
	}
	
	public BuySuggestionResponse getBuySuggestion(int productId) {
		Product product = productRepo.findById(productId)
	            .orElseThrow(() -> new ProductNotFoundException(productId));

	    Price latestPrice = priceRepo.findTopByProductOrderByDateDesc(product);

	    if (latestPrice == null) {
	        return new BuySuggestionResponse(
	                product.getName(),
	                BigDecimal.ZERO,
	                BigDecimal.ZERO,
	                BigDecimal.ZERO,
	                "NO DATA",
	                "No price data available yet. Please wait for the next scrape cycle."
	        );
	    }

	    BigDecimal currentPrice = latestPrice.getPrice();
	    BigDecimal lowestEver = priceRepo.findLowestPriceByProduct(product);
	    BigDecimal average = priceRepo.findAveragePriceByProduct(product)
	            .setScale(2, RoundingMode.HALF_UP);

	    String suggestion;
	    String reason;

	    if (currentPrice.compareTo(lowestEver) <= 0) {
	        suggestion = "BUY NOW";
	        reason = "Current price is the lowest ever recorded. This is the best price available.";
	    } else if (currentPrice.compareTo(average) < 0) {
	        suggestion = "GOOD TIME TO BUY";
	        reason = "Current price is below the average price. You are getting a decent deal.";
	    } else if (currentPrice.compareTo(average.multiply(new BigDecimal("1.10"))) <= 0) {
	        suggestion = "AVERAGE PRICE";
	        reason = "Current price is close to the average. No urgency either way.";
	    } else {
	        suggestion = "WAIT";
	        reason = "Current price is higher than average. Consider waiting for a better deal.";
	    }

	    return new BuySuggestionResponse(
	            product.getName(),
	            currentPrice,
	            lowestEver,
	            average,
	            suggestion,
	            reason
	    );
	}

	public PriceResponse convertToResponse(Price price) {
		return new PriceResponse(
				price.getProduct().getName(),
				price.getWebsite(),
				price.getPrice(),
				price.getDate()
		);
	}
}