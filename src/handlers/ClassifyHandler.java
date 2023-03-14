package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import catalogs.WineCatalog;
import utils.FileUtils;

public class ClassifyHandler {

	private static ClassifyHandler instance = null;
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream) throws ClassNotFoundException, IOException {
		String wine = (String) inStream.readObject();
		double rating = (int) inStream.readObject();
		WineCatalog wineCatalog = WineCatalog.getInstance();
		String result = "";
		if(!wineCatalog.wineExists(wine)) {
			result = "Wine " + wine + " doesn't exist, try again with another wine" + FileUtils.EOL;
		}else {
			wineCatalog.rate(wine, rating);
			result = "Successfully classified wine: " + wine + FileUtils.EOL;
		}
		outStream.writeObject(result);
	}
	
	public static ClassifyHandler getInstance() {
		if (instance == null) 
			instance = new ClassifyHandler();
		return instance;
	}

}
