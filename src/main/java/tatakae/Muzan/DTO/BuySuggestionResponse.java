package tatakae.Muzan.DTO;

import java.math.BigDecimal;

public class BuySuggestionResponse {

	private String productName;
	private BigDecimal currentPrice;
	private BigDecimal lowestEverPrice;
	private BigDecimal averagePrice;
	private String suggestion;
	private String reason;
	
	public BuySuggestionResponse(String productName, BigDecimal currentPrice, BigDecimal lowestEverPrice,
			BigDecimal averagePrice, String suggestion, String reason) {
		super();
		this.productName = productName;
		this.currentPrice = currentPrice;
		this.lowestEverPrice = lowestEverPrice;
		this.averagePrice = averagePrice;
		this.suggestion = suggestion;
		this.reason = reason;
	}

	public String getProductName() {
		return productName;
	}
	
	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}
	
	public BigDecimal getLowestEverPrice() {
		return lowestEverPrice;
	}
	
	public BigDecimal getAveragePrice() {
		return averagePrice;
	}
	
	public String getSuggestion() {
		return suggestion;
	}
	
	public String getReason() {
		return reason;
	}
	
}
