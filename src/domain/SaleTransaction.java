package domain;

public final class SaleTransaction extends Transaction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6632825857610453869L;
	private int numUnits;
	private double unitValue;
	
	public SaleTransaction(String uid, String wineid, int numUnits, double unitValue) {
		super(uid, wineid, "sell");
		
		this.numUnits = numUnits;
		this.unitValue = unitValue;
		
	}

	public int getNumUnits() {
		return numUnits;
	}


	public double getUnitValue() {
		return unitValue;
	}

}
