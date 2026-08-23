package tatakae.pricepulse.scraper;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import tatakae.pricepulse.exception.ScraperException;
import tatakae.pricepulse.scraper.JsoupBookScraper;

class JsoupBookScraperTest {

    private final JsoupBookScraper scraper = new JsoupBookScraper();

    @Test
    void getWebsiteName_returnsExpectedName() {
        assertEquals("books.toScrap", scraper.getWebsiteName());
    }

    @Test
    void fetchPrice_throwsScraperExceptionForInvalidUrl() {
        assertThrows(ScraperException.class,
                () -> scraper.fetchPrice("https://this-domain-does-not-exist-12345.com/book"));
    }

    @Test
    void fetchPrice_throwsScraperExceptionForMalformedUrl() {
        assertThrows(ScraperException.class,
                () -> scraper.fetchPrice("not-a-valid-url"));
    }
}