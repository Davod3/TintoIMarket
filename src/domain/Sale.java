package domain;

public class Sale {
	
	private String seller;
	private double value;
	private int quantity;
	private String wineName;
	
	public Sale(String seller, double value, int quantity, String name) {
		this.seller = seller;
		this.value = value;
		this.quantity = quantity;
		this.wineName = name;
		
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
	
	public String getWineName() {
		return this.wineName;
	}
}
