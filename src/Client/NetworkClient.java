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
			sendFile(imageFile);
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
			File f = receiveFile();
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
	
	private void sendFile(String fileName) {
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
	
	private File receiveFile() throws ClassNotFoundException, IOException {
		
		String name = (String) inStream.readObject();
		int size = (Integer) inStream.readObject();
		
		byte[] bytes = new byte[size];
		
		inStream.readFully(bytes, 0, size);
		
		File outFile = new File(name);
		
		FileOutputStream fout = new FileOutputStream(outFile);
		fout.write(bytes);
		
		return outFile;
	}
}
