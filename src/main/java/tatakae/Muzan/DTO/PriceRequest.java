package tatakae.Muzan.DTO;

import java.math.BigDecimal;

public class PriceRequest {

	private String website;
	private BigDecimal price;
		
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	
}
