package domain;

public class Sale {
	
	private String seller;
	private double value;
	private double quantity;
	
	public Sale(String seller, double value, int quantity) {
		this.seller = seller;
		this.value = value;
		this.quantity = quantity;
	}
	
	public String getSeller() {
		return seller;
	}
	
	public double getQuantity() {
		return quantity;
	}
	
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	public double getValue() {
		return value;
	}
}
