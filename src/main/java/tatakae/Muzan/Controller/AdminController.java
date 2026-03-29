package tatakae.Muzan.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tatakae.Muzan.Scraper.BookCatalogueImporter;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final BookCatalogueImporter importer;

    public AdminController(BookCatalogueImporter importer) {
        this.importer = importer;
    }

    @PostMapping("/import")
    public String importBooks() {
        log.info("Manual catalogue import triggered");
        int count = importer.importAll();
        return "Import complete. " + count + " books saved.";
    }
}
