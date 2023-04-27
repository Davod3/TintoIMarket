package domain;

public final class BuyTransaction extends Transaction{
	
	private static final long serialVersionUID = 7607571269903830799L;
	private int unitsSold;
	private double unitValue = 0;
	private String sellerid;
	
	public BuyTransaction(String uid, String wineid, int unitsSold, String sellerid) {
		super(uid, wineid, "buy");

		this.unitsSold = unitsSold;
		this.sellerid = sellerid;
	}

	public int getUnitsSold() {
		return unitsSold;
	}

	public double getUnitValue() {
		return unitValue;
	}
	
	public void setUnitValue(double value) {
		this.unitValue = value;
	}
	
	public String getSellerId() {
		return this.sellerid;
	}
	
	

}
