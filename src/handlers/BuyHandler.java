package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;

import catalogs.UserCatalog;
import catalogs.WineCatalog;
import domain.Sale;
import utils.FileUtils;

/**
 * The BuyHandler class represents the action of buying a wine. 
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class BuyHandler {
	
	private static BuyHandler instance = null;

	/**
	 * Buys the given quantity of the given wine from the given seller.
	 * If the given wine does not exist, or if there are not enough units
	 * available for sale, or if the buyer does not have enough money, sends
	 * an error message.
	 * 
	 * @param inStream					Stream for receiving input
	 * @param outStream					Stream for outputting result
	 * @param loggedUser				The buyer
	 * @throws ClassNotFoundException	When trying to find the class of an object
	 * 									that does not match/exist
	 * @throws IOException				When inStream does not receive input
	 * 									or the outStream can't send the result message			
	 * @throws NoSuchAlgorithmException 
	 */
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser)
			throws ClassNotFoundException, IOException, NoSuchAlgorithmException {
		//Read the name of the wine, the seller and the quantity to buy
		String wine = (String) inStream.readObject();
		String seller = (String) inStream.readObject();
		int quantity = (int) inStream.readObject();
		//Get Wine's Catalog only instance
		WineCatalog wineCatalog = WineCatalog.getInstance();
		//Create the result message
		String result = "";
		//Check if wine exists
		boolean wineExists = wineCatalog.wineExists(wine);
		if (!wineExists) {
			outStream.writeObject("Wine " + wine
					+ " doesn't exist, try again with another wine" + FileUtils.EOL);
			return;
		}
		
		System.out.println("Gets here 1");
		
		//Get the wine sale of the seller
		Sale sale = wineCatalog.getWineSaleBySeller(wine, seller);
		
		if(sale == null) {
			outStream.writeObject("User " + seller +" doesn't exist!" + FileUtils.EOL);
			return;
		}
		
		//Check if there are more or a equal number of units to buy
		boolean wineAvailable = sale.getQuantity() >= quantity;
		if(!wineAvailable) {
			outStream.writeObject("Only " + sale.getQuantity()
					+ " units available" + FileUtils.EOL);
			return;
		}
		//Check if buyer has enough money
		boolean buyerHasEnoughMoney = UserCatalog.getInstance().hasEnoughMoney(loggedUser, sale.getValue() * sale.getQuantity());
		if (!buyerHasEnoughMoney) {
			result = "You don't have enough money" + FileUtils.EOL;
		} else {
			//Withdraw from sale the quantity to be purchased
			sale.setQuantity(sale.getQuantity() - quantity);
			//Transfer the purchase money
			UserCatalog.getInstance().transfer(loggedUser, seller, sale.getValue() * quantity);
			//If there are not more units to buy from the seller, delete the sale
			if(sale.getQuantity() == 0)
				wineCatalog.removeSaleFromSeller(wine, seller);
			result = "Wine " + wine + " successfully bought!" + FileUtils.EOL;
			wineCatalog.updateWines();
		}
		//Send result message
		outStream.writeObject(result);
	}

	/**
	 * Returns the unique instance of the BuyHandler class.
	 * If there is no instance of the class, a new one is created and returned.
	 * 
	 * @return	the unique instance of the BuyHandler class
	 */
	public static BuyHandler getInstance() {
		if (instance == null)
			instance = new BuyHandler();
		return instance;
	}
}
