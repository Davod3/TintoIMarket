package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Catalogs.WineCatalog;
import domain.Sale;
import domain.Wine;
import utils.FileUtils;

public class ViewHandler {
	
	private static ViewHandler instance = null;
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser) throws ClassNotFoundException, IOException {
		StringBuilder result = new StringBuilder();
		WineCatalog wineCatalog = WineCatalog.getInstance();
		
		String wine = (String) inStream.readObject();
		
		if (wineCatalog.wineExists(wine)) {
			outStream.writeObject(true);
			Wine wineToView = wineCatalog.getWine(wine);
			//Image
			FileUtils.sendFile(wineToView.getImageName(), outStream);
			
			//Average classification
			result.append("Wine " + wine + " has a classification of " + wineToView.getRating() + " stars. \n");
			
			if (wineToView.hasSales()) {
				for (Sale sale: wineToView.getSales()) {
					result.append("User " + sale.getSeller()
					+ " is selling " + sale.getQuantity()
					+ " units of " + wine + " for the price of "
					+ sale.getValue() + " each unit. \n");
				}
			}
		}
		else {
			outStream.writeObject(false);
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
