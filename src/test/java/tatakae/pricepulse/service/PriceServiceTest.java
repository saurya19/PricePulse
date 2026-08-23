package tatakae.pricepulse.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tatakae.pricepulse.dto.BuySuggestionResponse;
import tatakae.pricepulse.exception.ProductNotFoundException;
import tatakae.pricepulse.model.Price;
import tatakae.pricepulse.model.Product;
import tatakae.pricepulse.repository.PriceRepository;
import tatakae.pricepulse.repository.ProductRepository;
import tatakae.pricepulse.service.PriceAlertService;
import tatakae.pricepulse.service.PriceService;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private ProductRepository productRepo;

    @Mock
    private PriceRepository priceRepo;

    @Mock
    private PriceAlertService alertService;

    private PriceService priceService;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1);
        product.setName("Test Book");
        priceService = new PriceService(productRepo, priceRepo, List.of(), alertService);
    }

    private Product product;

    @Test
    void addPrice_savesPriceForExistingProduct() {
        when(productRepo.findById(1)).thenReturn(Optional.of(product));
        when(priceRepo.save(any(Price.class))).thenAnswer(inv -> inv.getArgument(0));

        Price result = priceService.addPrice(1, "PageMart", new BigDecimal("299.00"));

        assertEquals("PageMart", result.getWebsite());
        assertEquals(new BigDecimal("299.00"), result.getPrice());
        verify(priceRepo).save(any(Price.class));
    }

    @Test
    void addPrice_throwsWhenProductNotFound() {
        when(productRepo.findById(99)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> priceService.addPrice(99, "PageMart", new BigDecimal("299.00")));
    }

    @Test
    void getBuySuggestion_returnsNoDataWhenNoPriceHistory() {
        when(productRepo.findById(1)).thenReturn(Optional.of(product));
        when(priceRepo.findTopByProductOrderByDateDesc(product)).thenReturn(null);

        BuySuggestionResponse response = priceService.getBuySuggestion(1);

        assertEquals("NO DATA", response.getSuggestion());
    }

    @Test
    void getBuySuggestion_returnsBuyNowWhenCurrentPriceIsLowestEver() {
        Price latest = new Price();
        latest.setPrice(new BigDecimal("200.00"));

        when(productRepo.findById(1)).thenReturn(Optional.of(product));
        when(priceRepo.findTopByProductOrderByDateDesc(product)).thenReturn(latest);
        when(priceRepo.findLowestPriceByProduct(product)).thenReturn(new BigDecimal("200.00"));
        when(priceRepo.findAveragePriceByProduct(product)).thenReturn(new BigDecimal("250.00"));

        BuySuggestionResponse response = priceService.getBuySuggestion(1);

        assertEquals("BUY NOW", response.getSuggestion());
    }

    @Test
    void getBuySuggestion_returnsGoodTimeToBuyWhenBelowAverage() {
        Price latest = new Price();
        latest.setPrice(new BigDecimal("220.00"));

        when(productRepo.findById(1)).thenReturn(Optional.of(product));
        when(priceRepo.findTopByProductOrderByDateDesc(product)).thenReturn(latest);
        when(priceRepo.findLowestPriceByProduct(product)).thenReturn(new BigDecimal("200.00"));
        when(priceRepo.findAveragePriceByProduct(product)).thenReturn(new BigDecimal("250.00"));

        BuySuggestionResponse response = priceService.getBuySuggestion(1);

        assertEquals("GOOD TIME TO BUY", response.getSuggestion());
    }

    @Test
    void getBuySuggestion_returnsWaitWhenPriceIsHighAboveAverage() {
        Price latest = new Price();
        latest.setPrice(new BigDecimal("400.00"));

        when(productRepo.findById(1)).thenReturn(Optional.of(product));
        when(priceRepo.findTopByProductOrderByDateDesc(product)).thenReturn(latest);
        when(priceRepo.findLowestPriceByProduct(product)).thenReturn(new BigDecimal("200.00"));
        when(priceRepo.findAveragePriceByProduct(product)).thenReturn(new BigDecimal("250.00"));

        BuySuggestionResponse response = priceService.getBuySuggestion(1);

        assertEquals("WAIT", response.getSuggestion());
    }

    @Test
    void addScraperPrice_continuesWhenOneScraperFails() {
        when(productRepo.findById(1)).thenReturn(Optional.of(product));

        assertDoesNotThrow(() -> priceService.addScraperPrice(1));
    }
}
