package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Catalogs.UserCatalog;
import Catalogs.WineCatalog;
import domain.Sale;
import domain.Wine;

public class SellHandler {
	
	private static SellHandler instance = null;
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser) throws ClassNotFoundException, IOException {
		String result = null;
		WineCatalog wineCatalog = WineCatalog.getInstance();
		
		String wine = (String) inStream.readObject();
		double value = (double) inStream.readObject();
		int quantity = (int) inStream.readObject();
		
		//If wine exists
		if (wineCatalog.wineExists(wine)) {
			Wine wineToSell = wineCatalog.getWine(wine);
			Sale sale = wineToSell.getSaleBySeller(loggedUser);
			
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
				sale = new Sale(loggedUser, value, quantity);
				wineToSell.addSale(sale);
			}
		}
		else {
			result = "Wine: " + wine + " doesn't exist, try again with another wine";
		}
		outStream.writeObject(result);
	}
	
	public static SellHandler getInstance() throws IOException {

		if (instance == null)
			instance = new SellHandler();
		return instance;

	}
}
