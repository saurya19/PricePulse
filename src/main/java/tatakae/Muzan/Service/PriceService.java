package tatakae.Muzan.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import tatakae.Muzan.Exception.ProductNotFoundException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import tatakae.Muzan.DTO.PriceRequest;
import tatakae.Muzan.DTO.PriceResponse;
import tatakae.Muzan.Model.Price;
import tatakae.Muzan.Model.Product;
import tatakae.Muzan.Scraper.PriceScraper;
import tatakae.Muzan.repository.PriceRepository;
import tatakae.Muzan.repository.ProductRepository;

@Service
public class PriceService {
	
	private final ProductRepository productRepo; 
	private final PriceRepository priceRepo;
	private final List<PriceScraper> scrapers;

	public PriceService(ProductRepository productRepo, PriceRepository priceRepo, List<PriceScraper> scrapers) {
	    this.productRepo = productRepo;
	    this.priceRepo = priceRepo;
	    this.scrapers = scrapers;
	}
	
	private static final Logger log = LoggerFactory.getLogger(PriceService.class);

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
	
	public Price getCheapestPrice(int productId) {
		
		Product product = productRepo.findById(productId)
			    .orElseThrow(() -> new ProductNotFoundException(productId));
		return priceRepo.findTopByProductOrderByPriceAsc(product);
		
	}
	
	public Price getLatestPrice(int productId) {
		Product product = productRepo.findById(productId)
			    .orElseThrow(() -> new ProductNotFoundException(productId));
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
	    
	    log.info("Started Scraping for Product Id {}", productId);;

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

	@Scheduled(fixedRateString = "${price.scrape.interval}")
	public void autoScrapePrice() {

		Pageable pageable = PageRequest.of(0,20);
	    List<Product> products = productRepo.findAll(pageable).getContent();

	    log.info("Scheduler started at {}", LocalDateTime.now());
	    
	    for (Product product : products) {

	        try {
	            addScraperPrice(product.getId());
	            log.info("Price updated for product ID: " + product.getId());

	        } catch (Exception e) {

	        	log.error("Failed to update product ID: {}", product.getId(), e);
	        }
	    }
	    log.info("Auto scrape cycle completed at {}", LocalDateTime.now());;
	}

	public PriceResponse convertToResponse(Price price) {
		return new PriceResponse(price.getProduct().getName(),
					price.getWebsite(),
					price.getPrice(),
					price.getDate()
					);
	}
	
}
