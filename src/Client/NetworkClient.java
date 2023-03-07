package Client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;

import domain.Message;

public class NetworkClient {

	private Socket clientSocket;
	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;
	
	public NetworkClient(String serverAddress) {
		String[] addressSplit = serverAddress.split(":");
		String host = addressSplit[0];
		int port = Integer.parseInt(addressSplit[1]);
		try {
			clientSocket = new Socket(host, port);
		} catch (IOException e) {
			System.out.println("Error creating socket");
		}
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
//			outStream.close();
//			inStream.close();
		} catch (IOException e) {
			System.out.println("Erro ao enviar user e password para a socket");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return validation;
	}
	
	public void add(String wine, String imageFile) {
		try {
			outStream.writeObject("add");
			outStream.writeObject(wine);
			sendFile(imageFile);
			System.out.println("Adding wine: " + wine);
		} catch (IOException e) {
			System.out.println("Error adding wine");
		}
	}
	
	public void sell(String wine, String value, String quantity) {
		try {
			outStream.writeObject("sell");
			outStream.writeObject(wine);
			outStream.writeObject(Double.parseDouble(value));
			outStream.writeObject(Integer.parseInt(quantity));
			System.out.println("Selling wine: " + wine);
		} catch (IOException e) {
			System.out.println("Error selling wine");
		}
	}
	
	public void view(String wine) {
		try {
			outStream.writeObject("view");
			outStream.writeObject(wine);
			System.out.println("Viewing wine: " + wine);
		} catch (IOException e) {
			System.out.println("Error viewing wine");
		}
	}
	
	public void buy(String wine, String seller, String quantity) {
		try {
			outStream.writeObject("buy");
			outStream.writeObject(wine);
			outStream.writeObject(seller);
			outStream.writeObject(Integer.parseInt(quantity));
			System.out.println("Bought wine: " + wine);
		} catch (IOException e) {
			System.out.println("Error buying wine");
		}
	}
	
	public void wallet() {
		try {
			outStream.writeObject("wallet");
			System.out.println("Getting wallet");
		} catch (IOException e) {
			System.out.println("Error getting wallet");
		}
	}
	
	public void classify(String wine, String stars) {
		try {
			outStream.writeObject("classify");
			outStream.writeObject(wine);
			outStream.writeObject(Integer.parseInt(stars));
			System.out.println("Classifying wine: " + wine);
		} catch (IOException e) {
			System.out.println("Error classifying wine");
		}
	}
	
	public void talk(String userFrom, String userTo, String message) {
		try {
			outStream.writeObject("talk");
			outStream.writeObject(userFrom);
			outStream.writeObject(userTo);
			Message m = new Message(userFrom, userTo, message);
			outStream.writeObject(m);
			System.out.println("Talking to user: " + userTo);
		} catch (IOException e) {
			System.out.println("Error talking to user");
		}
	}
	
	public void read() {
		try {
			outStream.writeObject("read");
			System.out.println("Reading new messages");
		} catch (IOException e) {
			System.out.println("Error reading messages");
		}
	}
	
	private void sendFile(String fileName) {
		File image = new File(fileName);
		int imageSize = (int)image.length();
		try {
			outStream.writeObject(fileName);
	        outStream.writeObject(imageSize);
	        byte[] buffer = Files.readAllBytes(image.toPath());
	        outStream.write(buffer, 0, imageSize);
		} catch (IOException e) {
			System.out.println("Error sending file: " + fileName);
		}
	}
}
