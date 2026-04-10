package tatakae.pricepulse.scraper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import tatakae.pricepulse.model.Product;
import tatakae.pricepulse.repository.ProductRepository;

@Component
public class BookCatalogueImporter {

    private static final Logger log = LoggerFactory.getLogger(BookCatalogueImporter.class);
    private static final String BASE_URL = "https://books.toscrape.com/catalogue/";

    private final ProductRepository productRepo;

    public BookCatalogueImporter(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public int importAll() {
        int count = 0;
        int page = 1;

        while (true) {
            try {
                String pageUrl = BASE_URL + "page-" + page + ".html";
                log.info("Crawling page {}", page);

                Document doc = Jsoup.connect(pageUrl)
                        .userAgent("Mozilla/5.0")
                        .get();

                Elements books = doc.select("article.product_pod");

                for (Element book : books) {
                    try {
                        String relativeUrl = book.select("h3 a").attr("href");
                        String bookUrl = BASE_URL + relativeUrl.replace("../", "");

                        if (productRepo.existsByUrl(bookUrl)) {
                            log.info("Already exists, skipping: {}", bookUrl);
                            continue;
                        }

                        Product product = scrapeBookPage(bookUrl);
                        if (product != null) {
                            productRepo.save(product);
                            count++;
                            log.info("Saved book: {}", product.getName());
                        }

                    } catch (Exception e) {
                        log.error("Failed to scrape book: {}", e.getMessage());
                    }
                }

                // check if next page exists
                Element nextButton = doc.select("li.next").first();
                if (nextButton == null) {
                    log.info("Last page reached. Total imported: {}", count);
                    break;
                }

                page++;

            } catch (Exception e) {
                log.error("Failed to crawl page {}: {}", page, e.getMessage());
                break;
            }
        }

        return count;
    }

    private Product scrapeBookPage(String bookUrl) throws Exception {
        Document doc = Jsoup.connect(bookUrl)
                .userAgent("Mozilla/5.0")
                .get();

        String name = doc.select("h1").text();

        String ratingWord = doc.select("p.star-rating").attr("class")
                .replace("star-rating ", "");
        int rating = convertRating(ratingWord);

        String imageRelativeUrl = doc.select("div.item.active img").attr("src");
        String imageUrl = "https://books.toscrape.com/" +
                imageRelativeUrl.replace("../../", "");

        String description = "";
        Element descElement = doc.select("article.product_page > p").first();
        if (descElement != null) {
            description = descElement.text();
        }

        String category = doc.select("ul.breadcrumb li").get(2).text();

        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setImageURL(imageUrl);
        product.setDescription(description);
        product.setRating(rating);
        product.setUrl(bookUrl);

        return product;
    }

    private int convertRating(String word) {
        return switch (word) {
            case "One" -> 1;
            case "Two" -> 2;
            case "Three" -> 3;
            case "Four" -> 4;
            case "Five" -> 5;
            default -> 0;
        };
    }
}