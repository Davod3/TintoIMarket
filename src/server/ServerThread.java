package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import catalogs.UserCatalog;
import handlers.*;

/**
 * This class represents the threads of the server of this application
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class ServerThread extends Thread {

	private Socket socket = null;
	private UserCatalog userCatalog;
	private String loggedUser = null;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;
	
	/**
	 * Creates a new thread for the server to manage
	 * the commands from the client
	 * 
	 * @param inSoc				Client's socket
	 * @throws IOException		When an I/O error occurs while 
	 * 							reading/writing to a file or stream
	 */
	public ServerThread(Socket inSoc) throws IOException {
		//Save client's socket
		this.socket = inSoc;
		//Get the unique instance of User Catalog
		this.userCatalog = UserCatalog.getInstance();
		//Open streams
		this.outStream = new ObjectOutputStream(socket.getOutputStream());
		this.inStream = new ObjectInputStream(socket.getInputStream());
		//Print message with result
		System.out.println("New connection established!");
	}

	/**
	 * Runs the engine of a server's thread
	 */
	public void run() {
		// Open IO streams
		try {
			//Get user and password from client
			String user = null;
			String pwd = null;
			user = (String) inStream.readObject();
			pwd = (String) inStream.readObject();
			//Validate client
			boolean value = false;
			if (value = (this.loggedUser = userCatalog.validate(user, pwd)) != null) {
				// User authenticated, wait for commands
				System.out.println("User authenticated");
				outStream.writeObject(value);
				mainLoop();
			} else {
				// User failed to authenticate, close connection
				System.out.println("Authentication failed");
				outStream.writeObject(value);
			}
		} catch (SocketException e) {
			try {
				System.out.println("User exited");
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes the commands from client
	 * 
	 * @throws ClassNotFoundException		When trying to find the class of an object
	 * 										that does not match/exist
	 * @throws IOException					When an I/O error occurs while
	 * 										reading/writing to a file or stream
	 */
	private void mainLoop() throws ClassNotFoundException, IOException {
		// Run main command execution logic
		while (this.socket.isConnected()) {
			//Get command
			System.out.println("Waiting for commands...");
			try {
				String command = (String) inStream.readObject();
				System.out.println("Command: " + command);
				//Run the command
				switch (command) {
				//Each command has a unique handler
				case "add":
					AddHandler.getInstance().run(inStream, outStream);
					break;
				case "sell":
					SellHandler.getInstance().run(inStream, outStream, loggedUser);
					break;
	
				case "view":
					ViewHandler.getInstance().run(inStream, outStream);
					break;
	
				case "buy":
					BuyHandler.getInstance().run(inStream, outStream, loggedUser);
					break;
	
				case "wallet":
					WalletHandler.getInstance().run(outStream, loggedUser);
					break;
				case "classify":
					ClassifyHandler.getInstance().run(inStream, outStream);
					break;
	
				case "talk":
					TalkHandler.getInstance().run(inStream, outStream, loggedUser);
					break;
	
				case "read":
					ReadHandler.getInstance().run(outStream, loggedUser);
					break;
	
				default:
					//Default case for wrong command message
					break;
				}
			} catch (EOFException e) {
				System.out.println("User exited");
			}
		}
	}
}
