package tatakae.pricepulse.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tatakae.pricepulse.model.PriceAlert;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${resend.api.key}")
    private String apiKey;

    private final HttpClient client = HttpClient.newHttpClient();

    public void sendPriceAlert(PriceAlert alert, String currentPrice, String website) {
        try {

            String jsonBody = """
            {
              "from": "onboarding@resend.dev",
              "to": ["%s"],
              "subject": "PricePulse Alert -- %s",
              "html": "<p>Good news! The price dropped.</p>
                       <p><b>Book:</b> %s</p>
                       <p><b>Target Price:</b> %s</p>
                       <p><b>Current Price:</b> %s</p>
                       <p><b>Website:</b> %s</p>"
            }
            """.formatted(
                    alert.getEmail(),
                    alert.getProduct().getName(),
                    alert.getProduct().getName(),
                    alert.getTargetPrice(),
                    currentPrice,
                    website
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("Resend response: {}", response.body());

        } catch (Exception e) {
            log.error("Failed to send email via Resend: {}", e.getMessage());
        }
    }
}