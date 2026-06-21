package tatakae.pricepulse.exception;

public class AlertAlreadyExistsException extends RuntimeException {

    public AlertAlreadyExistsException(String email, int productId) {
        super("Alert already exists for email: "
                + email
                + " and product id: "
                + productId);
    }
}