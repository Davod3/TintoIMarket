package domain;

/**
 * The Sale class represents a sale in this application.
 * Each sale has a seller, a value, a quantity and a wine associated.
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class Sale {
	
	private String seller;
	private double value;
	private int quantity;
	private String wineName;
	
	/**
	 * Creates a new Sale given the seller, the wine,
	 * the value and quantity
	 * 
	 * @param seller		The seller of the wine
	 * @param value			The value of each unit of the wine
	 * @param quantity		The quantity to put on sale
	 * @param name			The name of the wine
	 */
	public Sale(String seller, double value, int quantity, String name) {
		this.seller = seller;
		this.value = value;
		this.quantity = quantity;
		this.wineName = name;
	}
	
	/**
	 * Returns the seller of this wine
	 * 
	 * @return	The seller
	 */
	public String getSeller() {
		return seller;
	}
	
	/**
	 * Returns the quantity of this wine put on sale
	 * 
	 * @return	The quantity of this wine
	 */
	public synchronized int getQuantity() {
		return quantity;
	}
	
	/**
	 * Sets a new quantity for this wine
	 * 
	 * @param quantity	The new quantity for this wine
	 */
	public synchronized void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	/**
	 * Returns the value of each unit of this wine
	 * 
	 * @return	The value of each unit of this wine
	 */
	public synchronized double getValue() {
		return value;
	}
	
	/**
	 * Sets a new value for each unit of this wine
	 * 
	 * @param value		The new value for each unit of this wine
	 */
	public synchronized void setValue(double value) {
		this.value = value;
	}
	
	/**
	 * Returns the name of this wine
	 * 
	 * @return	The name of this wine
	 */
	public String getWineName() {
		return this.wineName;
	}
}
