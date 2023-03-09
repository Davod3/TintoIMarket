package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Catalogs.UserCatalog;
import domain.Message;
import domain.User;

public class TalkHandler {
	
	private static TalkHandler instance = null;
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser) throws ClassNotFoundException, IOException {
		String result = null;
		
		String user = (String) inStream.readObject();
		String msg = (String) inStream.readObject();
		UserCatalog userCatalog = UserCatalog.getInstance();
		User currentUser = userCatalog.getUser(loggedUser);
		User toUser = userCatalog.getUser(user);
		
		if (toUser != null) {
			Message msgTosend = new Message(loggedUser, user, msg);
			currentUser.addMessage(msgTosend);
		}
		else {
			result = "User: " + user + " doesn't exist, try again with another user";
		}
		
		outStream.writeObject(result);
	}
	
	public static TalkHandler getInstance() throws IOException {

		if (instance == null)
			instance = new TalkHandler();
		return instance;

	}
}
