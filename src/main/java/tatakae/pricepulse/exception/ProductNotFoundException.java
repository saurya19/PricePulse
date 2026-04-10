package tatakae.pricepulse.exception;

public class ProductNotFoundException extends RuntimeException{

	public ProductNotFoundException(int productId) {
		super("Product with ID: " + productId + " not found.");
	}
	
}
