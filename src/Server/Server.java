package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	private int sPort;
	private boolean close = false;

	public Server(int port) {
		sPort = port;
	}

	public void run() {
		
		ServerSocket sSoc = null;
		
		try {
			sSoc = new ServerSocket(this.sPort);
		} catch (IOException e) {
			
			System.out.println("Failed to open new server socket!");
			System.out.println(e.getMessage());
		}
		
		//Wait for connections
		while(!this.close) {
			
			try {
				Socket inSoc = sSoc.accept();
				ServerThread workerThread = new ServerThread(inSoc);
				
			} catch (IOException e) {
				
				System.out.println("Failed to connect to client!");
				System.out.println(e.getMessage());
			}
			
		}
		
		
	}
	
	public void stop() {
		this.close = true;
	}
	
	

}
