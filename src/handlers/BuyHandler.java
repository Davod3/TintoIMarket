package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import catalogs.UserCatalog;
import catalogs.WineCatalog;
import domain.Sale;

public class BuyHandler {

	public final String EOL = System.lineSeparator();
	private static BuyHandler instance = null;

	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser)
			throws ClassNotFoundException, IOException {

		String wine = (String) inStream.readObject();
		String seller = (String) inStream.readObject();
		int quantity = (int) inStream.readObject();
		WineCatalog wineCatalog = WineCatalog.getInstance();

		
		String result = "";
		boolean wineExists = wineCatalog.wineExists(wine);
		
		if (!wineExists) {
			outStream.writeObject("Wine " + wine + " doesn't exist, try again with another wine" + EOL);
			return;
		} 
		
		Sale sale = wineCatalog.getWineSaleBySeller(wine, seller);	
		boolean wineAvailable = sale.getQuantity() >= quantity;
		
		if(!wineAvailable) {
			outStream.writeObject("Only " + sale.getQuantity() + " units available" + EOL);
			return;
		}
		
		boolean buyerHasEnoughMoney = UserCatalog.getInstance().hasEnoughMoney(loggedUser, sale.getValue() * sale.getQuantity());

		if (!buyerHasEnoughMoney) {
			result = "You don't have enough money" + EOL;
		} else {
			sale.setQuantity(sale.getQuantity() - quantity);
			UserCatalog.getInstance().transfer(loggedUser, seller, sale.getValue() * quantity);
			if(sale.getQuantity() == 0)
				wineCatalog.removeSaleFromSeller(wine, seller);
			result = "Wine " + wine + " successfully bought!" + EOL;
			wineCatalog.updateWines();
		}
		outStream.writeObject(result);
	}

	public static BuyHandler getInstance() throws IOException {

		if (instance == null)
			instance = new BuyHandler();
		return instance;
	}
}
