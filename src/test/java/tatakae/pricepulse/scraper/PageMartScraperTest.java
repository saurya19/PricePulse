package tatakae.pricepulse.scraper;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import tatakae.pricepulse.scraper.PageMartScraper;

class PageMartScraperTest {

    private final PageMartScraper scraper = new PageMartScraper();

    @Test
    void getWebsiteName_returnsExpectedName() {
        assertEquals("PageMart", scraper.getWebsiteName());
    }

    @Test
    void fetchPrice_returnsValueWithinExpectedRange() {
        BigDecimal price = scraper.fetchPrice("any-url");

        assertTrue(price.compareTo(new BigDecimal("2140")) >= 0);
        assertTrue(price.compareTo(new BigDecimal("6419")) <= 0);
    }
}