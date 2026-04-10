package tatakae.pricepulse.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import tatakae.pricepulse.dto.PriceAlertRequest;
import tatakae.pricepulse.model.PriceAlert;
import tatakae.pricepulse.service.PriceAlertService;

@Tag(name = "Price Alerts", description = "Email alert registration endpoints")
@RestController
@RequestMapping("/alerts")
public class PriceAlertController {

	private static final Logger log = LoggerFactory.getLogger(PriceAlertController.class);
	
	private final PriceAlertService alertService;
	
	public PriceAlertController(PriceAlertService alertService) {
		this.alertService = alertService;
	}
	
	@Operation(summary = "Register a new price alert")
	@PostMapping
	public ResponseEntity<PriceAlert> createAlert(@Valid @RequestBody PriceAlertRequest request){
		log.info("Alert Registration request for product {} by {}", request.getProductId(), request.getEmail());
		PriceAlert alert = alertService.createPriceAlert(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(alert);
	}
	
	@Operation(summary = "Get all registered alerts")
	@GetMapping
	public List<PriceAlert> getAllAlerts(){
		return alertService.getAllAlerts();
	}
	
	@Operation(summary = "Manually trigger alert check")
	@PostMapping("/check")
	public String checkAlerts() {
	    alertService.checkAndTriggerAlerts();
	    return "Alert check complete";
	}
	
}
