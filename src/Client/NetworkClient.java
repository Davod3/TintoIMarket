package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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

}
