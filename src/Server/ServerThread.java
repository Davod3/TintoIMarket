package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import catalogs.UserCatalog;
import handlers.*;

public class ServerThread extends Thread {

	private Socket socket = null;
	private UserCatalog userCatalog;
	private String loggedUser = null;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;
	

	public ServerThread(Socket inSoc) throws IOException {
		this.socket = inSoc;
		this.userCatalog = UserCatalog.getInstance();
		this.outStream = new ObjectOutputStream(socket.getOutputStream());
		this.inStream = new ObjectInputStream(socket.getInputStream());

		System.out.println("New connection established!");
	}

	public void run() {

		// Open IO streams
		try {

			String user = null;
			String pwd = null;

			user = (String) inStream.readObject();
			pwd = (String) inStream.readObject();

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void mainLoop() throws ClassNotFoundException, IOException {
		// Run main command execution logic

		while (this.socket.isConnected()) {

			System.out.println("Waiting for commands...");

			String command = (String) inStream.readObject();
			
			System.out.println("Command: " + command);
			
			switch (command) {
			
			case "add":
				AddHandler.getInstance().run(inStream, outStream, loggedUser);
				break;
			case "sell":
				SellHandler.getInstance().run(inStream, outStream, loggedUser);
				break;

			case "view":
				ViewHandler.getInstance().run(inStream, outStream, loggedUser);
				break;

			case "buy":
				BuyHandler.getInstance().run(inStream, outStream, loggedUser);
				break;

			case "wallet":
				WalletHandler.getInstance().run(outStream, loggedUser);
				break;
			case "classify":
				ClassifyHandler.getInstance().run(inStream, outStream, loggedUser);
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

		}

	}
}
