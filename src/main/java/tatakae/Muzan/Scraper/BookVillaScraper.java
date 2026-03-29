package tatakae.Muzan.Scraper;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class BookVillaScraper implements PriceScraper{

	@Override
	public BigDecimal fetchPrice(String productURL) {
		return new BigDecimal(300 + new Random().nextInt(901));
	}
	
	@Override
	public String getWebsiteName() {
		return "BookVilla";
	}
}
