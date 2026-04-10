package tatakae.pricepulse.scraper;

import java.math.BigDecimal;

public interface PriceScraper {

	BigDecimal fetchPrice(String productURL);
	String getWebsiteName();
	
}
