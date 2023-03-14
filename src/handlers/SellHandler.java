package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import catalogs.WineCatalog;
import domain.Sale;

public class SellHandler {
	
	public final String EOL = System.lineSeparator();
	private static SellHandler instance = null;
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser) throws ClassNotFoundException, IOException {
		WineCatalog wineCatalog = WineCatalog.getInstance();
		
		String wine = (String) inStream.readObject();
		double value = (double) inStream.readObject();
		int quantity = (int) inStream.readObject();
		
		String result = null;
		//If wine exists
		if (wineCatalog.wineExists(wine)) {
			Sale sale = wineCatalog.getWineSaleBySeller(wine, loggedUser);
			
			//If sale already exists
			if (sale != null) {
				sale.setQuantity(sale.getQuantity() + quantity);
				//If value is same
				if (sale.getValue() != value) {
					//Set new value
					sale.setValue(value);
				}
			}
			else {
				sale = new Sale(loggedUser, value, quantity, wine);
				wineCatalog.addSaleToWine(wine, sale);
			}
			result = "Wine " + wine + " has been successfully put on sale" + EOL;
		}
		else {
			result  = "Wine " + wine + " doesn't exist, try again with another wine" + EOL;
		}
		outStream.writeObject(result);
	}
	
	public static SellHandler getInstance() throws IOException {

		if (instance == null)
			instance = new SellHandler();
		return instance;

	}
}
