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
	
	public String add(String wine, String imageFile) {
		String result = "";
		try {
			outStream.writeObject("add");
			outStream.writeObject(wine);
			sendFile(imageFile);
			System.out.println("Adding wine: " + wine);
			result = (String) inStream.readObject();
			System.out.println("RESULT: "+result);
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
			System.out.println("Selling wine: " + wine);
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
			System.out.println("Viewing wine: " + wine);
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
			System.out.println("Bought wine: " + wine);
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
			System.out.println("Getting wallet");
			result = (String) inStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error buying wine");
		}  
		return result;
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
		File file = new File(fileName);
		int size = (int)file.length();
		try {
			outStream.writeObject(fileName);
	        outStream.writeObject(size);
	        byte[] buffer = new byte[size];
	        FileInputStream fin = new FileInputStream(file);
	        fin.read(buffer);
	        outStream.write(buffer, 0, size);
	        fin.close();
		} catch (IOException e) {
			System.out.println("Error sending file: " + fileName);
		}
	}
	
	private File receiveFile() throws ClassNotFoundException, IOException {
		
		String name = (String) inStream.readObject();
		int size = (Integer) inStream.readObject();
		
		byte[] bytes = new byte[size];
		
		inStream.read(bytes, 0, size);
		
		File outFile = new File(name);
		
		FileOutputStream fout = new FileOutputStream(outFile);
		fout.write(bytes);
		
		return outFile;
	}
}
