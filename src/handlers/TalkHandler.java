package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import catalogs.UserCatalog;
import domain.Message;
import utils.FileUtils;

public class TalkHandler {
	
	private static TalkHandler instance = null;
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser) throws ClassNotFoundException, IOException {
		String result = null;
		
		String user = (String) inStream.readObject();
		String msg = (String) inStream.readObject();
		UserCatalog userCatalog = UserCatalog.getInstance();
		
		if (userCatalog.exists(user)) {
			Message msgTosend = new Message(loggedUser, user, msg);
			userCatalog.addMessageToUser(user, msgTosend);
			result = "Message successfully sent to " + user + FileUtils.EOL;
		}
		else {
			result = "User " + user + " doesn't exist, try again with another user" + FileUtils.EOL;
		}
		
		outStream.writeObject(result);
	}
	
	public static TalkHandler getInstance() {

		if (instance == null)
			instance = new TalkHandler();
		return instance;

	}
}
