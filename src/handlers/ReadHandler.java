package handlers;

import java.io.IOException;
import java.io.ObjectOutputStream;

import Catalogs.UserCatalog;

public class ReadHandler {

	public static ReadHandler instance = null;

	public void run(ObjectOutputStream outStream, String loggedUser) throws IOException {
		outStream.writeObject(UserCatalog.getInstance().readMessages(loggedUser));
	}
	
	public static ReadHandler getInstance() {
		if (instance == null) 
			instance = new ReadHandler();
		return instance;
	}
}
