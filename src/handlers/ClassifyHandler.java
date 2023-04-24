package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import catalogs.WineCatalog;
import utils.FileIntegrityViolationException;
import utils.FileUtils;

/**
 * The ClassifyHandler class represents the action of rating a wine.
 * 
 * @author André Dias nº 55314
 * @author David Pereira nº 56361
 * @author Miguel Cut nº 56339
 */
public class ClassifyHandler {

	private static ClassifyHandler instance = null;

	/**
	 * Rates the given wine with the given rating. If the given wine does not exist,
	 * returns an error message.
	 * 
	 * @param inStream  Stream for receiving input
	 * @param outStream Stream for outputting result
	 * @throws ClassNotFoundException When trying to find the class of an object
	 *                                that does not match/exist
	 * @throws IOException            When inStream does not receive input or the
	 *                                outStream can't send the result message
	 * @throws NoSuchAlgorithmException 
	 * @throws FileIntegrityViolationException 
	 * @throws InvalidKeyException 
	 */
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream)
			throws ClassNotFoundException, IOException, NoSuchAlgorithmException, FileIntegrityViolationException, InvalidKeyException {
		// Read the name of the wine and the rating
		String wine = (String) inStream.readObject();
		double rating = (int) inStream.readObject();
		// Get Wine's Catalog only instance
		WineCatalog wineCatalog = WineCatalog.getInstance();
		// Create the result message
		String result = "";
		// Check if wine exists
		if (!wineCatalog.wineExists(wine)) {
			result = "Wine " + wine + " doesn't exist, try again with another wine" + FileUtils.EOL;
		} else if (rating > 5 || rating < 0) {
			result = "Rating must be between 0 and 5 stars!" + FileUtils.EOL;
		} else {
			// Rate the wine
			wineCatalog.rate(wine, rating);
			result = "Successfully classified wine: " + wine + FileUtils.EOL;
		}
		// Send result message
		outStream.writeObject(result);
	}

	/**
	 * Returns the unique instance of the ClassifyHandler class. If there is no
	 * instance of the class, a new one is created and returned.
	 * 
	 * @return the unique instance of the ClassifyHandler class
	 */
	public static ClassifyHandler getInstance() {
		if (instance == null)
			instance = new ClassifyHandler();
		return instance;
	}
}
