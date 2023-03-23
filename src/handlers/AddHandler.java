package handlers;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import catalogs.WineCatalog;
import utils.FileUtils;

/**
 * The AddHandler class represents the action of
 * adding a wine to the wine's catalog
 * (which has the information of all wines).
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class AddHandler {
	
	private static AddHandler instance = null;
	public static final String SERVER_IMAGES_DIR = "server_files/images/";
	
	/**
	 * Adds a new wine given the name of the wine and a image (both sent through streams).
	 * In case where there is already a wine with that name, returns an message error.
	 * Initially, the wine has no rating and no units available in the market.
	 * 
	 * @param inStream					Stream for receiving input
	 * @param outStream					Stream for outputting result
	 * @throws ClassNotFoundException	When trying to find the class of an object
	 * 									that does not match/exist
	 * @throws IOException				When inStream does not receive input
	 * 									(in this case, the name of the wine)
	 * 									or the outStream can't send the result message
	 */
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream)
			throws ClassNotFoundException, IOException {
		//Read wine name and image file of the wine
		String wine = (String) inStream.readObject();
		File received = FileUtils.receiveFile(SERVER_IMAGES_DIR, inStream);
		//Try to create a new Wine with the obtained wine and image
		boolean result = WineCatalog.getInstance().createWine(wine, received);
		//Output the result message
		if(result) {
			outStream.writeObject("Wine " + wine
					+ " succesfully registered!" + FileUtils.EOL);
		} else {
			outStream.writeObject("Failed to add wine. "
					+ wine + " already exists." + FileUtils.EOL);
		}
	}
	
	/**
	 * Returns the unique instance of the AddHandler class.
	 * If there is no instance of the class, a new one is created and returned.
	 * 
	 * @return	the unique instance of the AddHandler class
	 */
	public static AddHandler getInstance() {
		if (instance == null) 
			instance = new AddHandler();
		return instance;
	}
}
