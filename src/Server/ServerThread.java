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
		this.socket = inSoc;
		System.out.println("New connection established!");
		
		this.userCatalog = UserCatalog.getInstance();
	}
	
	public void run() {
		
		//Open IO streams
		try {
			
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			
			String user = (String) inStream.readObject();
			String pwd = (String) inStream.readObject();
			
			if(userCatalog.validate(user,pwd)) {
				//User authenticated, wait for commands
			} else {
				//User failed to authenticate, close connection
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
