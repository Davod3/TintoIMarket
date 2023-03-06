package Server;

import java.net.Socket;

public class ServerThread extends Thread {
	
	private Socket socket = null;
	
	public ServerThread(Socket inSoc) {
		this.socket = inSoc;
		System.out.println("New connection established!");
	}
	
	public void run() {
		
		//Wait for messages
		
	}

}
