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
	
		System.out.println("Gets here 1");
		String wine = (String) inStream.readObject();
		System.out.println("Gets here 2");
		File received = receiveFile(inStream);
		
		System.out.println("gets here 5");
		
		boolean result = WineCatalog.getInstance().createWine(wine, received, loggedUser);
		
		System.out.println("gets here 6 " + result);
		
		if(result) {
			outStream.writeObject("Wine " + wine + " succesfully registered!");
		} else {
			outStream.writeObject("Failed to add wine. " + wine + " already exists.");
		}
	}
	
	private File receiveFile(ObjectInputStream inStream) throws ClassNotFoundException, IOException {
		
		System.out.println("gets here 3");
		
		String name = (String) inStream.readObject();
		int size = (int) inStream.readObject();
		
		System.out.println(name);
		System.out.println(size);
		
		byte[] bytes = new byte[size];
		
		System.out.println("gets here 3.5");
		
		inStream.read(bytes);
		//inStream.read(bytes, 0, size); //Error
		
		System.out.println("gets here 3.6");
		
		File outFile = new File(name);
		
		System.out.println("gets here 3.7");
		
		FileOutputStream fout = new FileOutputStream(outFile);
		
		System.out.println("gets here 3.8");
		fout.write(bytes);
		
		System.out.println("gets here 3.9");
		
		fout.close();
		
		System.out.println("Gets here 4");
		
		return outFile;
	}
	
	public static AddHandler getInstance() throws IOException {
		
		if (instance == null) 
			instance = new AddHandler();
		return instance;
	}
}
