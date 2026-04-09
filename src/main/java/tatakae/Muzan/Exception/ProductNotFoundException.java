package tatakae.Muzan.Exception;

public class ProductNotFoundException extends RuntimeException{

	public ProductNotFoundException(int productId) {
		super("Product with ID: " + productId + " not found.");
	}
	
}
