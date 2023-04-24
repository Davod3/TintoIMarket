package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

import catalogs.UserCatalog;
import domain.Message;
import utils.FileUtils;

/**
 * The TalkHandler class represents the action of
 * sending messages to other users. 
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class TalkHandler {
	
	private static TalkHandler instance = null;
	
	/**
	 * Sends the given message to the given user.
	 * If the given user does not exist, send an error message.
	 * 
	 * @param inStream					Stream for receiving input
	 * @param outStream					Stream for outputting result		
	 * @param loggedUser				The user who wants to send the message
	 * @throws ClassNotFoundException	When trying to find the class of an object
	 * 									that does not match/exist
	 * @throws IOException				When inStream does not receive input
	 * 									or the outStream can't send the result message		
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser)
			throws ClassNotFoundException, IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		//Create result message
		String result = "";
		//Read user's name and the message to be sent to the user
		String user = (String) inStream.readObject();
		byte[] encryptedMsg = (byte[]) inStream.readObject();
		//Get User's Catalog only instance
		UserCatalog userCatalog = UserCatalog.getInstance();
		//Check if user exists
		if (userCatalog.exists(user)) {
			//Create message
			Message msgTosend = new Message(loggedUser, user, encryptedMsg);
			//Send message to user
			userCatalog.addMessageToUser(user, msgTosend);
			result = "Message successfully sent to " + user + FileUtils.EOL;
		}
		else {
			result = "User " + user
					+ " doesn't exist, try again with another user" + FileUtils.EOL;
		}
		//Send result message
		outStream.writeObject(result);
	}
	
	/**
	 * Returns the unique instance of the TalkHandler class.
	 * If there is no instance of the class, a new one is created and returned.
	 * 
	 * @return	the unique instance of the TalkHandler class
	 */
	public static TalkHandler getInstance() {
		if (instance == null)
			instance = new TalkHandler();
		return instance;
	}
}
