package tatakae.Muzan.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import tatakae.Muzan.Service.PriceAlertService;

import tatakae.Muzan.DTO.PriceAlertRequest;
import tatakae.Muzan.Model.PriceAlert;

@RestController
@RequestMapping("/alerts")
public class PriceAlertController {

	private static final Logger log = LoggerFactory.getLogger(PriceAlertController.class);
	
	private final PriceAlertService alertService;
	
	public PriceAlertController(PriceAlertService alertService) {
		this.alertService = alertService;
	}
	
	@PostMapping
	public ResponseEntity<PriceAlert> createAlert(@Valid @RequestBody PriceAlertRequest request){
		log.info("Alert Registration request for product {} by {}", request.getProductId(), request.getEmail());
		PriceAlert alert = alertService.createPriceAlert(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(alert);
	}
	
	@GetMapping
	public List<PriceAlert> getAllAlerts(){
		return alertService.getAllAlerts();
	}
	
	@PostMapping("/check")
	public String checkAlerts() {
	    alertService.checkAndTriggerAlerts();
	    return "Alert check complete";
	}
	
}
