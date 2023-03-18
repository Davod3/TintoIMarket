package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class represents the server of this application
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class Server {
	
	private int sPort;
	private boolean close = false;

	/**
	 * Creates a new Server given the port
	 * 
	 * @param port		The port for the connection to the server
	 */
	public Server(int port) {
		sPort = port;
	}

	/**
	 * Runs the server's engine
	 */
	public void run() {
		//Create socket for server
		ServerSocket sSoc = null;
		//Open socket on the defined port
		try {
			sSoc = new ServerSocket(this.sPort);
			System.out.println("Listening on port " + sPort);
		} catch (IOException e) {
			System.out.println("Failed to open new server socket!");
			System.out.println(e.getMessage());
		}
		
		//Wait for connections
		while(!this.close) {
			try {
				//Try to catch new connections to clients
				Socket inSoc = sSoc.accept();
				//Create a new thread for the client
				ServerThread workerThread = new ServerThread(inSoc);
				workerThread.start();
			} catch (IOException e) {
				System.out.println("Failed to connect to client!");
				System.out.println(e.getMessage());
			}
		}
	}
	
	/**
	 * Closes the socket and all connections of the server
	 */
	public void stop() {
		this.close = true;
	}
}
