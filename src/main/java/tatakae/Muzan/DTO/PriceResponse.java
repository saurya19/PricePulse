package tatakae.Muzan.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PriceResponse {

	private String productName;
	private String website;
	private BigDecimal price;
	private LocalDateTime time;
	
	public PriceResponse(String productName, String website, BigDecimal price, LocalDateTime time) {
		super();
		this.productName = productName;
		this.website = website;
		this.price = price;
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
	
	
}
