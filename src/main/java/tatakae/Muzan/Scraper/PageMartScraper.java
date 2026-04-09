package tatakae.Muzan.Scraper;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class PageMartScraper implements PriceScraper{

	@Override
	public BigDecimal fetchPrice(String productURL) {
		return new BigDecimal(2140 + new Random().nextInt(4279));
	}
	
	@Override
	public String getWebsiteName() {
		return "PageMart";
	}
	
}
