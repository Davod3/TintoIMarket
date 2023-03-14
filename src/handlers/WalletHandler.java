package handlers;

import java.io.IOException;
import java.io.ObjectOutputStream;

import catalogs.UserCatalog;
import utils.FileUtils;

public class WalletHandler {
	
	private static WalletHandler instance = null;
	
	public void run(ObjectOutputStream outStream, String loggedUser) throws IOException {
		UserCatalog userCatalog = UserCatalog.getInstance();
		outStream.writeObject("Your current balance is " + userCatalog.getUser(loggedUser).getBalance() + FileUtils.EOL);
	}
	
	public static WalletHandler getInstance() {

		if (instance == null)
			instance = new WalletHandler();
		return instance;

	}
}
