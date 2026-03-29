package tatakae.Muzan.Controller;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tatakae.Muzan.DTO.PriceRequest;
import tatakae.Muzan.DTO.PriceResponse;
import tatakae.Muzan.Model.Price;
import tatakae.Muzan.Model.Product;
import tatakae.Muzan.Scraper.BookVillaScraper;
import tatakae.Muzan.Service.PriceService;
import tatakae.Muzan.repository.PriceRepository;
import tatakae.Muzan.repository.ProductRepository;


@RestController
@RequestMapping("/prices")
public class PriceController {

	private final PriceService priceService;
	public PriceController(PriceService priceService) {
		this.priceService = priceService;
	}
	
	private final static Logger log = LoggerFactory.getLogger(PriceController.class);
	
	@PostMapping("/{productId}")
	public PriceResponse addPrices(@PathVariable int productId,@Valid @RequestBody PriceRequest request) {
		
		Price price =  priceService.addPrice(productId, request.getWebsite(), request.getPrice());
		return priceService.convertToResponse(price);
	}
	
	@GetMapping("/{productId}")
	public Page<PriceResponse> getPrice(
	        @PathVariable int productId,
	        @RequestParam(defaultValue="0") int page,
	        @RequestParam(defaultValue="5") int size) {

	    Page<Price> pricePage = priceService.getPrice(productId, page, size);

	    return pricePage.map(price -> priceService.convertToResponse(price));
	}
	
	@GetMapping("/{productId}/latest")
	public PriceResponse latestPrices(@PathVariable int productId) {
		
		Price price = priceService.getLatestPrice(productId);
		return priceService.convertToResponse(price);
		
	}
	
	@GetMapping("/{productId}/cheapest")
	public PriceResponse cheapPrice(@PathVariable int productId) {
		
		Price price = priceService.getCheapestPrice(productId);
		return priceService.convertToResponse(price);
		
	}
	
	@GetMapping("/{productId}/scrape")
	public void scrapeAndSave(@PathVariable int productId) {

		log.info("Manual scrape triggered for product id: {}", productId);
	    priceService.addScraperPrice(productId);
	    
	}

}
