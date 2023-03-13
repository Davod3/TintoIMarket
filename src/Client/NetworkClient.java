package Client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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
	
	public String add(String wine, String imageFile) throws IOException, ClassNotFoundException {
		String result = "";
		
		outStream.writeObject("add");
		outStream.flush();
		outStream.writeObject(wine);
		outStream.flush();
		FileUtils.sendFile(imageFile, outStream);
		System.out.println("Adding wine: " + wine);
		result = (String) inStream.readObject();
		
		return result;
	}
	
	public String sell(String wine, String value, String quantity) throws IOException, ClassNotFoundException {
		String result = "";
		
		outStream.writeObject("sell");
		outStream.writeObject(wine);
		outStream.writeObject(Double.parseDouble(value));
		outStream.writeObject(Integer.parseInt(quantity));
		result = (String) inStream.readObject();

		return result;
	}
	
	public String view(String wine) throws IOException, ClassNotFoundException {
		String result = "";

		outStream.writeObject("view");
		outStream.writeObject(wine);
		boolean wineExists = inStream.readBoolean();
		if(wineExists)
			FileUtils.receiveFile(inStream);
		result = (String) inStream.readObject();

		return result;
	}
	
	public String buy(String wine, String seller, String quantity) throws IOException, ClassNotFoundException {
		String result = "";
		
		outStream.writeObject("buy");
		outStream.writeObject(wine);
		outStream.writeObject(seller);
		outStream.writeObject(Integer.parseInt(quantity));
		result = (String) inStream.readObject();
		 
		return result;
	}
	
	public String wallet() throws IOException, ClassNotFoundException {
		String result = "";
		
		outStream.writeObject("wallet");
		result = (String) inStream.readObject();
			
		return result;
	}
	
	public String classify(String wine, String stars) throws IOException, ClassNotFoundException {
		String result = "";
		
		outStream.writeObject("classify");
		outStream.writeObject(wine);
		outStream.writeObject(Integer.parseInt(stars));
		result = (String) inStream.readObject();
		 
		return result;
	}
	
	public String talk(String userFrom, String userTo, String message) throws IOException, ClassNotFoundException {
		String result = "";
		
		outStream.writeObject("talk");
		outStream.writeObject(userTo);
		outStream.writeObject(message);
		result = (String) inStream.readObject();
		
		return result;
	}
	
	public String read() throws IOException, ClassNotFoundException {
		String result = "";
		
		outStream.writeObject("read");
		result = (String) inStream.readObject();
			
		return result;
	}
}
