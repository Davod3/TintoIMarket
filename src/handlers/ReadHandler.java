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
	 * @param outStream		Stream for outputting messages
	 * @param loggedUser	The user we want to read the messages for
	 * @throws IOException	When outStream can't send the result message
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public void run(ObjectOutputStream outStream, String loggedUser)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException {
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
