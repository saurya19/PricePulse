package tatakae.Muzan.DTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PriceResponse {

	private String productName;
	private String website;
	private BigDecimal price;
	@JsonProperty("displayPrice")
	private String displayPrice;
	private LocalDateTime time;
	
	public PriceResponse(String productName, String website, BigDecimal price, LocalDateTime time) {
		super();
		this.productName = productName;
		this.website = website;
		this.price = price;
		this.displayPrice = "₹" + price.setScale(2, RoundingMode.HALF_UP);
		this.time = time;
	}

	public String getProductName() {
		return productName;
	}

	public String getWebsite() {
		return website;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public LocalDateTime getTime() {
		return time;
	}
	
	
	public String getDisplayPrice() {
		return displayPrice; 
	}
	
}
