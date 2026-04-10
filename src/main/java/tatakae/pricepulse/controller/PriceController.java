package tatakae.pricepulse.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import tatakae.pricepulse.dto.BuySuggestionResponse;
import tatakae.pricepulse.dto.PriceRequest;
import tatakae.pricepulse.dto.PriceResponse;
import tatakae.pricepulse.model.Price;
import tatakae.pricepulse.service.PriceService;

@Tag(name = "Price", description = "Price tracking and scraping endpoints")
@RestController
@RequestMapping("/prices")
public class PriceController {

	private final PriceService priceService;
	public PriceController(PriceService priceService) {
		this.priceService = priceService;
	}
	
	private final static Logger log = LoggerFactory.getLogger(PriceController.class);
	
	@Operation(summary = "Add price manually")
	@PostMapping("/{productId}")
	public PriceResponse addPrices(@PathVariable int productId,@Valid @RequestBody PriceRequest request) {
		
		Price price =  priceService.addPrice(productId, request.getWebsite(), request.getPrice());
		return priceService.convertToResponse(price);
	}
	
	@Operation(summary = "Get full price history paginated")
	@GetMapping("/{productId}")
	public Page<PriceResponse> getPrice(
	        @PathVariable int productId,
	        @RequestParam(defaultValue="0") int page,
	        @RequestParam(defaultValue="5") int size) {

	    Page<Price> pricePage = priceService.getPrice(productId, page, size);

	    return pricePage.map(price -> priceService.convertToResponse(price));
	}
	
	@Operation(summary = "Get latest price for a product")
	@GetMapping("/{productId}/latest")
	public PriceResponse latestPrices(@PathVariable int productId) {
		
		Price price = priceService.getLatestPrice(productId);
		return priceService.convertToResponse(price);
		
	}
	
	@Operation(summary = "Get cheapest ever price for a product")
	@GetMapping("/{productId}/cheapest")
	public PriceResponse cheapPrice(@PathVariable int productId) {
		
		Price price = priceService.getCheapestPrice(productId);
		return priceService.convertToResponse(price);
		
	}
	
	@Operation(summary = "Trigger manual price scrape for a product")
	@GetMapping("/{productId}/scrape")
	public void scrapeAndSave(@PathVariable int productId) {

		log.info("Manual scrape triggered for product id: {}", productId);
	    priceService.addScraperPrice(productId);
	    
	}
	
	@Operation(summary = "Get buy suggestion based on price history")
	@GetMapping("/{productId}/suggestion")
	public BuySuggestionResponse getBuySuggestion(@PathVariable int productId) {
		return priceService.getBuySuggestion(productId);
	}

}
