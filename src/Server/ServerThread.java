package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Catalogs.UserCatalog;

public class ServerThread extends Thread {
	
	private Socket socket = null;
	private UserCatalog userCatalog;
	
	public ServerThread(Socket inSoc) {
		System.out.println("Opened clietn socket");
		this.socket = inSoc;
		System.out.println("New connection established!");
		
		this.userCatalog = UserCatalog.getInstance();
	}
	
	public void run() {
		
		//Open IO streams
		try {
			
			System.out.println("Got here");
			
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			
			System.out.println("Gets here after streams");
			
			String user = null;
			String pwd = null;
			
			System.out.println(inStream.available());
			
			user = (String) inStream.readObject();
			pwd = (String) inStream.readObject();
			
			boolean value = false;
			
			if(value = userCatalog.validate(user,pwd)) {
				//User authenticated, wait for commands
				System.out.println("User authenticated");
				outStream.writeObject(value);
			} else {
				//User failed to authenticate, close connection
				System.out.println("Authentication failed");
				
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
