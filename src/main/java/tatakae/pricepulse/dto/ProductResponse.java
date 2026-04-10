package tatakae.pricepulse.dto;

public class ProductResponse {
	
	private int productId;
	private int rating;
	private String name;
	private String category;
	private String imageURL;
	private String description;
	
	public ProductResponse(int productId, String name, String category, int rating, String imageURL, String description) {
		super();
		this.productId = productId;
		this.name = name;
		this.category = category;
		this.rating = rating;
		this.imageURL = imageURL;
		this.description = description;
	}

	public int getProductId() {
		return productId;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public int getRating() {
		return rating;
	}

	public String getImageURL() {
		return imageURL;
	}

	public String getDescription() {
		return description;
	}

}
