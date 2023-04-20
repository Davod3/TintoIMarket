package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;

import catalogs.WineCatalog;
import domain.Sale;
import utils.FileIntegrityViolationException;
import utils.FileUtils;

/**
 * The SellHandler class represents the action of selling a wine. 
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class SellHandler {
	
	private static SellHandler instance = null;
	
	/**
	 * Puts on sale the given quantity of the given wine for the given price.
	 * If the given wine does not exist, send an error message.
	 * 
	 * @param inStream					Stream for receiving input
	 * @param outStream					Stream for outputting result		
	 * @param loggedUser				The user who is selling the wine
	 * @throws ClassNotFoundException	When trying to find the class of an object
	 * 									that does not match/exist
	 * @throws IOException				When inStream does not receive input
	 * 									or the outStream can't send the result message		
	 * @throws NoSuchAlgorithmException 
	 * @throws FileIntegrityViolationException 
	 */
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser)
			throws ClassNotFoundException, IOException, NoSuchAlgorithmException, FileIntegrityViolationException {
		//Get Wine's Catalog only instance
		WineCatalog wineCatalog = WineCatalog.getInstance();
		//Read the name of the wine, the price and the quantity to sell
		String wine = (String) inStream.readObject();
		double value = (double) inStream.readObject();
		int quantity = (int) inStream.readObject();
		//Create result message
		String result = "";
		//Check if wine exists
		if (wineCatalog.wineExists(wine)) {
			//Attempt to see if there is already a sale on this wine by this user
			Sale sale = wineCatalog.getWineSaleBySeller(wine, loggedUser);
			//If sale already exists
			if (sale != null) {
				//Add the units to the quantity already there
				sale.setQuantity(sale.getQuantity() + quantity);
				//If the price is not the same
				if (sale.getValue() != value) {
					//Set new price
					sale.setValue(value);
				}
			}
			//If sale does not exist
			else {
				//Create new sale and update Wine's Catalog
				sale = new Sale(loggedUser, value, quantity, wine);
				wineCatalog.addSaleToWine(wine, sale);
			}
			result = "Wine " + wine
					+ " has been successfully put on sale" + FileUtils.EOL;
		}
		else {
			result  = "Wine " + wine
					+ " doesn't exist, try again with another wine" + FileUtils.EOL;
		}
		//Send result message
		outStream.writeObject(result);
	}
	
	/**
	 * Returns the unique instance of the SellHandler class.
	 * If there is no instance of the class, a new one is created and returned.
	 * 
	 * @return	the unique instance of the SellHandler class
	 */
	public static SellHandler getInstance() {
		if (instance == null)
			instance = new SellHandler();
		return instance;
	}
}
