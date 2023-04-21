package handlers;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

import catalogs.UserCatalog;
import utils.FileUtils;

/**
 * The WalletHandler class represents the action of
 * getting the current balance of a user. 
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class WalletHandler {
	
	private static WalletHandler instance = null;
	
	/**
	 * Sends the current balance of the given loggedUser.
	 * 
	 * @param outStream			Stream for outputting result	
	 * @param loggedUser		The user for which we want to see and send the balance
	 * @throws IOException		When outStream can't send the message with the balance
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public void run(ObjectOutputStream outStream, String loggedUser)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		//Get user's Catalog only instance
		UserCatalog userCatalog = UserCatalog.getInstance();
		//Send message with loggedUser's balance
		outStream.writeObject("Your current balance is "
				+ userCatalog.getUser(loggedUser).getBalance() + FileUtils.EOL);
	}
	
	/**
	 * Returns the unique instance of the WalletHandler class.
	 * If there is no instance of the class, a new one is created and returned.
	 * 
	 * @return	the unique instance of the WalletHandler class
	 */
	public static WalletHandler getInstance() {
		if (instance == null)
			instance = new WalletHandler();
		return instance;
	}
}
