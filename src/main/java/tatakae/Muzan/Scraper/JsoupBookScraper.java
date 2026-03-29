package tatakae.Muzan.Scraper;

import java.math.BigDecimal;

import org.jsoup.Jsoup;
import tatakae.Muzan.Exception.ScraperException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JsoupBookScraper implements PriceScraper {

	private static final Logger log = LoggerFactory.getLogger(JsoupBookScraper.class);
	
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
            
            String priceText = priceElement.text().replace("£", "");
            BigDecimal price =  new BigDecimal(priceText);
            log.info("Scraper success. Website: {}, Price: {}", getWebsiteName(), price);
            
            return price;
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

