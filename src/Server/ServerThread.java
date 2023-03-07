package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Catalogs.UserCatalog;
import domain.User;

public class ServerThread extends Thread {
	
	private Socket socket = null;
	private UserCatalog userCatalog;
	private User loggedUser = null;
	
	public ServerThread(Socket inSoc) throws IOException {
		System.out.println("Opened clietn socket");
		this.socket = inSoc;
		System.out.println("New connection established!");
		
		this.userCatalog = UserCatalog.getInstance();
	}
	
	public void run() {
		
		//Open IO streams
		try {
			
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
		
			
			String user = null;
			String pwd = null;
			
			System.out.println(inStream.available());
			
			user = (String) inStream.readObject();
			pwd = (String) inStream.readObject();
			
			boolean value = false;
			
			if(value = (this.loggedUser = userCatalog.validate(user,pwd)) != null) {
				//User authenticated, wait for commands
				System.out.println("User authenticated");
				outStream.writeObject(value);
			} else {
				//User failed to authenticate, close connection
				System.out.println("Authentication failed");
				outStream.writeObject(value);
			}
		
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
