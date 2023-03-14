package handlers;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import catalogs.WineCatalog;
import utils.FileUtils;

public class AddHandler {
	
	public final String EOL = System.lineSeparator();
	private static AddHandler instance = null;
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser) throws ClassNotFoundException, IOException {
	
		String wine = (String) inStream.readObject();
		File received = FileUtils.receiveFile(inStream);		
		boolean result = WineCatalog.getInstance().createWine(wine, received);
				
		if(result) {
			outStream.writeObject("Wine " + wine + " succesfully registered!" + EOL);
		} else {
			outStream.writeObject("Failed to add wine. " + wine + " already exists." + EOL);
		}
	}
	
	public static AddHandler getInstance() throws IOException {
		
		if (instance == null) 
			instance = new AddHandler();
		return instance;
	}
}
