package tatakae.Muzan.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import tatakae.Muzan.Model.PriceAlert;

@Service
public class EmailService {

	private static final Logger log = LoggerFactory.getLogger(EmailService.class);
	
	private final JavaMailSender mailSender;
	
	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	public  void sendPriceAlert(PriceAlert alert, String currentPrice, String website) {
		try {
			
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(alert.getEmail());
			message.setSubject("PricePulse Alert --" + alert.getProduct().getName());
			message.setText(
					"Good news! The price you were tracking has dropped.\n\n" +
			                "Book: " + alert.getProduct().getName() + "\n" +
			                "Your target price: " + alert.getTargetPrice() + "\n" +
			                "Current price on " + website + ": " + currentPrice + "\n\n" +
			                "Visit books.toscrape.com to buy now.\n\n" +
			                "— PricePulse"
					);
			mailSender.send(message);
			log.info("Alert Email sent to {} for product {}", alert.getEmail(), alert.getProduct().getName());
			
		}catch(Exception e) {
			log.error("Failed to send Email to {}:{}", alert.getEmail(), e.getMessage());
		}
	}
	
}
