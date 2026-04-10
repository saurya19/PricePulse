package tatakae.pricepulse.scraper;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import tatakae.pricepulse.exception.ScraperException;

@Component
public class JsoupBookScraper implements PriceScraper {

	private static final Logger log = LoggerFactory.getLogger(JsoupBookScraper.class);
	
	private static final BigDecimal GBP_TO_INR = new BigDecimal("107.00");
	
    @Override
    public BigDecimal fetchPrice(String productUrl) {

    	log.info("Scraper started for URL: {}", productUrl);
    	
        try {
        	Document doc = Jsoup.connect(productUrl)
                    .userAgent("Mozilla/5.0")
                    .get();

            Element priceElement = doc.select(".price_color").first();
            
            if (priceElement == null) {
                log.error("Price element not found for URL: {}", productUrl);
                throw new ScraperException("Failed to scrape from " + getWebsiteName());
            }
            
            String priceText = priceElement.text()
                    .replace("£", "")
                    .replace("Â", "")
                    .trim();
            BigDecimal gbpPrice = new BigDecimal(priceText);
            BigDecimal inrPrice = gbpPrice.multiply(GBP_TO_INR).setScale(2, RoundingMode.HALF_UP);
            log.info("Scraper success. GBP: {}, INR: {}", gbpPrice, inrPrice);
            return inrPrice;
        } 
        catch (Exception e) {
        	
        	log.error("Scraper failed for URL: {}", productUrl, e);

        	throw new ScraperException("Failed to scrape from " + getWebsiteName(), e);
        }
    }

    @Override
    public String getWebsiteName() {
        return "books.toScrap";
    }
}

