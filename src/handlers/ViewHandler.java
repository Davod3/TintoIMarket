package handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Catalogs.WineCatalog;
import domain.Sale;
import domain.Wine;

public class ViewHandler {
	
	private static ViewHandler instance = null;
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser) throws ClassNotFoundException, IOException {
		StringBuilder result = new StringBuilder();
		WineCatalog wineCatalog = WineCatalog.getInstance();
		
		String wine = (String) inStream.readObject();
		
		if (wineCatalog.wineExists(wine)) {
			Wine wineToView = wineCatalog.getWine(wine);
			//Image
			sendFile(inStream, outStream, wineToView.getImageName());
			
			//Average classification
			result.append("Wine " + wine + " has a classification of " + wineToView.getRating() + " stars. \n");
			
			if (wineToView.hasSales()) {
				for (Sale sale: wineToView.getSales()) {
					result.append("User " + sale.getSeller()
					+ " is selling " + sale.getQuantity()
					+ " units of " + wine + " for the price of "
					+ sale.getValue() + " each unit. \n");
				}
			}
		}
		else {
			result.append("Wine: " + wine + " doesn't exist, try again with another wine");
		}
		
		outStream.writeObject(result.toString());
	}
	
	private void sendFile(ObjectInputStream inStream, ObjectOutputStream outStream, String fileName) {
		File file = new File(fileName);
		long size = file.length();
		try {
			outStream.writeObject(fileName);
			byte[] buffer = new byte[(int) size];
	        outStream.writeObject(buffer.length);
	        System.out.println();
	        FileInputStream fin = new FileInputStream(file);
	        fin.read(buffer);
	        outStream.write(buffer, 0, buffer.length);
	        outStream.flush();
	        fin.close();
		} catch (IOException e) {
			System.out.println("Error sending file: " + fileName);
		}
	}
	
	public static ViewHandler getInstance() throws IOException {

		if (instance == null)
			instance = new ViewHandler();
		return instance;

	}
}
