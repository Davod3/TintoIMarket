package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Catalogs.UserCatalog;
import Catalogs.WineCatalog;
import domain.Sale;

public class BuyHandler {

	private static BuyHandler instance = null;
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser) throws ClassNotFoundException, IOException {
		
		String wine = (String) inStream.readObject();
		String seller = (String) inStream.readObject();
		int quantity = (int) inStream.readObject();
		WineCatalog wineCatalog = WineCatalog.getInstance();
		Sale sale = wineCatalog.getWine(wine).getSaleBySeller(seller);
		String result = "";
		boolean wineExists = wineCatalog.wineExists(wine);
		boolean wineAvailable = sale.getQuantity() >= quantity;
		boolean buyerHasEnoughMoney = UserCatalog.getInstance().hasEnoughMoney(loggedUser, sale.getValue());
		if(!wineExists) {
			result = "Wine doesn't exist, try with another wine";
		} else if(!wineAvailable) {
			result = "Only " + sale.getQuantity() + " units available";
		}else if(buyerHasEnoughMoney) {
			result = "You don't have enough money";
		}else {
			sale.setQuantity(sale.getQuantity() - quantity);
			UserCatalog.getInstance().transfer(loggedUser, seller, sale.getValue()*quantity);
			result = "Wine " + wine + " successfully bought!";
		}
		outStream.writeObject(result);
	}
	
	public static BuyHandler getInstance() throws IOException {

		if (instance == null) 
			instance = new BuyHandler();
		return instance;
	}
}
