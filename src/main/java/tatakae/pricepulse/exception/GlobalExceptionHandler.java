package tatakae.pricepulse.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import tatakae.pricepulse.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ProductNotFoundException e) {
    	
    	log.error("Exception occured {}", e.getMessage(), e);
    	ErrorResponse error =
    	        new ErrorResponse(
    	                404,
    	                "Not Found",
    	                e.getMessage()
    	        );

    	return ResponseEntity
    	        .status(HttpStatus.NOT_FOUND)
    	        .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
    	
    	log.error("Exception occured {}", e.getMessage(), e);
    	ErrorResponse error =
    	        new ErrorResponse(
    	                400,
    	                "Bad Request",
    	                "Invalid input data"
    	        );

    	return ResponseEntity
    	        .badRequest()
    	        .body(error);
    }
    
    @ExceptionHandler(ScraperException.class)
    public ResponseEntity<ErrorResponse> handleScraperException(ScraperException e) {
    	
    	log.error("Scraper Failed {}", e.getMessage(), e);
    	ErrorResponse error =
    	        new ErrorResponse(
    	                500,
    	                "Internal Server Error",
    	                e.getMessage()
    	        );

    	return ResponseEntity
    	        .status(HttpStatus.INTERNAL_SERVER_ERROR)
    	        .body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {

        log.error("Unexpected error occurred", e);

        ErrorResponse error =
                new ErrorResponse(
                        500,
                        "Internal Server Error",
                        "An unexpected error occurred"
                );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
    
    @ExceptionHandler(AlertAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse>
    handleAlertAlreadyExists(AlertAlreadyExistsException e) {

        ErrorResponse error =
                new ErrorResponse(
                        409,
                        "Conflict",
                        e.getMessage()
                );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }
}
