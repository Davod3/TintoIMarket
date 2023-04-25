package handlers;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

import catalogs.UserCatalog;

/**
 * The ReadHandler class represents the action of reading messages sent from all users. 
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class ReadHandler {

	public static ReadHandler instance = null;

	/**
	 * Reads new messages sent from users to the logged user.
	 * Read messages are removed from server mailBox.
	 * 
	 * @param outStream								Stream for outputting messages
	 * @param loggedUser							The user we want to read the messages for
	 * @throws IOException							When outStream can't send the result message
	 * @throws InvalidAlgorithmParameterException 	If an invalid algorithm parameter is passed to a method
	 * @throws NoSuchPaddingException 				If the padding scheme is not available
	 * @throws InvalidKeySpecException 				If the requested key specification is invalid
	 * @throws NoSuchAlgorithmException 			If the requested algorithm is not available
	 * @throws InvalidKeyException 					If the key is invalid
	 * @throws ClassNotFoundException 				When trying to find the class of an object
	 * 												that does not match/exist
	 */
	public void run(ObjectOutputStream outStream, String loggedUser)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, ClassNotFoundException {
		outStream.writeObject(UserCatalog.getInstance().readMessages(loggedUser));
	}
	
	/**
	 * Returns the unique instance of the ReadHandler class.
	 * If there is no instance of the class, a new one is created and returned.
	 * 
	 * @return	the unique instance of the ReadHandler class
	 */
	public static ReadHandler getInstance() {
		if (instance == null) 
			instance = new ReadHandler();
		return instance;
	}
}
