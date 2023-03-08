package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SellHandler {
	
	private static SellHandler instance = null;
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser) {
		
	}
	
	public static SellHandler getInstance() throws IOException {

		if (instance == null)
			instance = new SellHandler();
		return instance;

	}
}
