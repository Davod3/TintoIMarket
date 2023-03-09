package handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Catalogs.WineCatalog;

public class AddHandler {
	
	private static AddHandler instance = null;
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser) throws ClassNotFoundException, IOException {
	
		String wine = (String) inStream.readObject();
		File received = receiveFile(inStream);		
		boolean result = WineCatalog.getInstance().createWine(wine, received, loggedUser);
				
		if(result) {
			outStream.writeObject("Wine " + wine + " succesfully registered!");
		} else {
			outStream.writeObject("Failed to add wine. " + wine + " already exists.");
		}
	}
	
	private File receiveFile(ObjectInputStream inStream) throws ClassNotFoundException, IOException {

		String name = (String) inStream.readObject();
		int size = (int) inStream.readObject();
		byte[] bytes = new byte[size];			
		inStream.readFully(bytes, 0, size);
				
		File outFile = new File(name);
				
		FileOutputStream fout = new FileOutputStream(outFile);
		fout.write(bytes);			
		fout.close();		
		return outFile;
	}
	
	public static AddHandler getInstance() throws IOException {
		
		if (instance == null) 
			instance = new AddHandler();
		return instance;
	}
}
