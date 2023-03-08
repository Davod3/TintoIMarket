package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Catalogs.WineCatalog;

public class ClassifyHandler {

	private static ClassifyHandler instance = null;
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser) throws ClassNotFoundException, IOException {
		String wine = (String) inStream.readObject();
		int rating = (int) inStream.readObject();
		WineCatalog wineCatalog = WineCatalog.getInstance();
		String result = "";
		if(!wineCatalog.wineExists(wine)) {
			result = "Wine: " + wine + " doesn't exist, try again with another wine";
		}else {
			wineCatalog.rate(wine, rating);
			result = "Successfully classified wine: " + wine;
		}
		outStream.writeObject(result);
	}
	
	public static ClassifyHandler getInstance() throws IOException {
		if (instance == null) 
			instance = new ClassifyHandler();
		return instance;
	}

}
