package Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;

import domain.Message;
import utils.FileUtils;

public class NetworkClient {

	private Socket clientSocket;
	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;
	private static final String DEFAULT_PORT = "12345";
	
	public NetworkClient(String serverAddress) throws UnknownHostException, IOException {
		
		//Check if address contains port, otherwise use default
		
		String[] addressSplit;
		
		if(serverAddress.contains(":")) {
			addressSplit = serverAddress.split(":");
		} else {
			
			addressSplit = new String[2];
			addressSplit[0] = serverAddress;
			addressSplit[1] = DEFAULT_PORT;
		}
		
		
		String host = addressSplit[0];
		int port = Integer.parseInt(addressSplit[1]);
		clientSocket = new Socket(host, port);
		createStreams();
	}

	private void createStreams() {
		try {
			inStream = new ObjectInputStream(clientSocket.getInputStream());
			outStream = new ObjectOutputStream(clientSocket.getOutputStream());

		} catch (IOException e) {
			System.out.println("Erro a criar streams");
		}
	}
	
	public boolean validateSession(String user, String password) {
		boolean validation = false;
		try {
			
			outStream.writeObject(user);
			outStream.writeObject(password);
			validation = (boolean) inStream.readObject();
			System.out.println(validation);
		} catch (IOException e) {
			System.out.println("Erro ao enviar user e password para a socket");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return validation;
	}
	
	public String add(String wine, String imageFile) {
		String result = "";
		try {
			outStream.writeObject("add");
			outStream.flush();
			outStream.writeObject(wine);
			outStream.flush();
			FileUtils.sendFile(imageFile, outStream);
			System.out.println("Adding wine: " + wine);
			result = (String) inStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error adding wine");
		} 
		return result;
	}
	
	public String sell(String wine, String value, String quantity) {
		String result = "";
		try {
			outStream.writeObject("sell");
			outStream.writeObject(wine);
			outStream.writeObject(Double.parseDouble(value));
			outStream.writeObject(Integer.parseInt(quantity));
			result = (String) inStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error selling wine");
		}  
		return result;
	}
	
	public String view(String wine) {
		String result = "";
		try {
			outStream.writeObject("view");
			outStream.writeObject(wine);
			boolean wineExists = (boolean) inStream.readObject();
			File f = null;
			if(wineExists)
				f = FileUtils.receiveFile(inStream);
			result = (String) inStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error viewing wine");
		}  
		return result;
	}
	
	public String buy(String wine, String seller, String quantity) {
		String result = "";
		try {
			outStream.writeObject("buy");
			outStream.writeObject(wine);
			outStream.writeObject(seller);
			outStream.writeObject(Integer.parseInt(quantity));
			result = (String) inStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error buying wine");
		}  
		return result;
	}
	
	public String wallet() {
		String result = "";
		try {
			outStream.writeObject("wallet");
			result = (String) inStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error buying wine");
		}  
		return result;
	}
	
	public String classify(String wine, String stars) {
		String result = "";
		try {
			outStream.writeObject("classify");
			outStream.writeObject(wine);
			outStream.writeObject(Integer.parseInt(stars));
			result = (String) inStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error classifying wine");
		}  
		return result;
	}
	
	public String talk(String userFrom, String userTo, String message) {
		String result = "";
		try {
			outStream.writeObject("talk");
			outStream.writeObject(userTo);
			outStream.writeObject(message);
			result = (String) inStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error talking to user");
		}  
		return result;
	}
	
	public String read() {
		String result = "";
		try {
			outStream.writeObject("read");
			result = (String) inStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error reading messages");
		}  
		return result;
	}
}
