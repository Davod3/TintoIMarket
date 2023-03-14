package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import catalogs.WineCatalog;
import domain.Sale;
import domain.Wine;
import utils.FileUtils;

public class ViewHandler {
	
	private static ViewHandler instance = null;
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream) throws ClassNotFoundException, IOException {
		StringBuilder result = new StringBuilder();
		WineCatalog wineCatalog = WineCatalog.getInstance();
		
		String wine = (String) inStream.readObject();
		
		if (wineCatalog.wineExists(wine)) {
			outStream.writeBoolean(true);
			Wine wineToView = wineCatalog.getWine(wine);
			//Image
			FileUtils.sendFile(wineToView.getImageName(), outStream);
			
			//Average classification
			result.append("Wine " + wine + " has a classification of " + wineToView.getRating() + " stars." + FileUtils.EOL);
			
			if (wineToView.hasSales()) {
				for (Sale sale: wineToView.getSales()) {
					result.append("User " + sale.getSeller()
					+ " is selling " + sale.getQuantity()
					+ " units of " + wine + " for the price of "
					+ sale.getValue() + " each unit." + FileUtils.EOL);
				}
			}
		}
		else {
			outStream.writeBoolean(false);
			result.append("Wine " + wine + " doesn't exist, try again with another wine" + FileUtils.EOL);
		}
		
		outStream.writeObject(result.toString());
	}
	
	public static ViewHandler getInstance() {

		if (instance == null)
			instance = new ViewHandler();
		return instance;

	}
}
