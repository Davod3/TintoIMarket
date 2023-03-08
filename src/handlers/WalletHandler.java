package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class WalletHandler {
	
	private static WalletHandler instance = null;
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser) {
		
	}
	
	public static WalletHandler getInstance() throws IOException {

		if (instance == null)
			instance = new WalletHandler();
		return instance;

	}
}
