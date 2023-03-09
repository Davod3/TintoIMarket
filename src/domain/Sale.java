package domain;

public class Sale {
	
	private String seller;
	private double value;
	private int quantity;
	
	public Sale(String seller, double value, int quantity) {
		this.seller = seller;
		this.value = value;
		this.quantity = quantity;
	}
	
	public String getSeller() {
		return seller;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
}
