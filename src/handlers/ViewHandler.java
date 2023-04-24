package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import catalogs.WineCatalog;
import domain.Sale;
import domain.Wine;
import utils.FileIntegrityViolationException;
import utils.FileUtils;

/**
 * The ViewHandler class represents the action of
 * viewing the information of a wine. 
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class ViewHandler {
	
	private static ViewHandler instance = null;
	public static final String SERVER_IMAGES_DIR = "server_files/images/";
	
	/**
	 * Returns the information associated to a wine.
	 * If the given wine does not exist, send an error message.
	 * 
	 * @param inStream					Stream for receiving input
	 * @param outStream					Stream for outputting result				
	 * @throws ClassNotFoundException	When trying to find the class of an object
	 * 									that does not match/exist
	 * @throws IOException				When inStream does not receive input
	 * 									or the outStream can't send the result message		
	 * @throws NoSuchAlgorithmException 
	 * @throws FileIntegrityViolationException 
	 * @throws InvalidKeyException 
	 */
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream)
			throws ClassNotFoundException, IOException, NoSuchAlgorithmException, FileIntegrityViolationException, InvalidKeyException {
		//Create result message
		StringBuilder result = new StringBuilder();
		//Get Wine's Catalog only instance
		WineCatalog wineCatalog = WineCatalog.getInstance();
		//Read the name of the wine
		String wine = (String) inStream.readObject();
		//Check if wine exists
		if (wineCatalog.wineExists(wine)) {
			//Send confirmation to user so he can expect to receive more information
			outStream.writeBoolean(true);
			//Get the wine
			Wine wineToView = wineCatalog.getWine(wine);
			//Send wine's image
			FileUtils.sendFile(SERVER_IMAGES_DIR + wineToView.getImageName(), outStream);
			//Get wine's rating
			result.append("Wine " + wine + " has a classification of "
					+ wineToView.getRating() + " stars." + FileUtils.EOL);
			//Check if there are sales for this wine
			if (wineToView.hasSales()) {
				//Get each sale information
				for (Sale sale: wineToView.getSales()) {
					result.append("User " + sale.getSeller()
					+ " is selling " + sale.getQuantity()
					+ " units of " + wine + " for the price of "
					+ sale.getValue() + " each unit." + FileUtils.EOL);
				}
			}
		}
		else {
			//Send confirmation to user that the wine does not exist
			outStream.writeBoolean(false);
			result.append("Wine " + wine 
					+ " doesn't exist, try again with another wine" + FileUtils.EOL);
		}
		//Send result message
		outStream.writeObject(result.toString());
	}
	
	/**
	 * Returns the unique instance of the ViewHandler class.
	 * If there is no instance of the class, a new one is created and returned.
	 * 
	 * @return	the unique instance of the ViewHandler class
	 */
	public static ViewHandler getInstance() {
		if (instance == null)
			instance = new ViewHandler();
		return instance;
	}
}
