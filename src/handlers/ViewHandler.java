package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Catalogs.WineCatalog;
import domain.Sale;
import domain.Wine;

public class ViewHandler {
	
	private static ViewHandler instance = null;
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser) throws ClassNotFoundException, IOException {
		StringBuilder result = new StringBuilder();
		WineCatalog wineCatalog = WineCatalog.getInstance();
		
		String wine = (String) inStream.readObject();
		
		if (wineCatalog.wineExists(wine)) {
			Wine wineToView = wineCatalog.getWine(wine);
			//Image
			
			//Average classification
			result.append(wineToView.getRating());
			
			//outStream.writeObject(wineToView.get);
			
			if (wineToView.hasSales()) {
				for (Sale sale: wineToView.getSales()) {
					result.append("User " + sale.getSeller()
					+ " is selling " + sale.getQuantity()
					+ " units of " + wine + " for the price of "
					+ sale.getValue() + " each unit.");
				}
			}
		}
		else {
			result.append("Wine: " + wine + " doesn't exist, try again with another wine");
		}
		
		outStream.writeObject(result.toString());
	}
	
	public static ViewHandler getInstance() throws IOException {

		if (instance == null)
			instance = new ViewHandler();
		return instance;

	}
}
