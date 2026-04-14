package tatakae.pricepulse.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import tatakae.pricepulse.scraper.BookCatalogueImporter;
import tatakae.pricepulse.service.PriceService;

@Tag(name = "Admin", description = "Admin operations")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final BookCatalogueImporter importer;
    private final PriceService priceService; // ✅ ADD THIS

    public AdminController(BookCatalogueImporter importer, PriceService priceService) { // ✅ ADD THIS
        this.importer = importer;
        this.priceService = priceService; // ✅ ADD THIS
    }

    @Operation(summary = "Import full book catalogue from books.toscrape")
    @PostMapping("/import")
    public String importBooks() {
        log.info("Manual catalogue import triggered");
        int count = importer.importAll();
        log.info("Import done. Starting price scrape for all products...");
        priceService.autoScrapeAllProducts(); // ✅ ADD THIS
        return "Import complete. " + count + " books saved. Prices scraped.";
    }
}
