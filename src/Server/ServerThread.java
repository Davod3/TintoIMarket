package Server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Catalogs.UserCatalog;
import Catalogs.WineCatalog;
import domain.User;
import handlers.*;

public class ServerThread extends Thread {

	private Socket socket = null;
	private UserCatalog userCatalog;
	private WineCatalog wineCatalog;
	private String loggedUser = null;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;
	

	public ServerThread(Socket inSoc) throws IOException {
		this.socket = inSoc;
		this.userCatalog = UserCatalog.getInstance();
		this.wineCatalog = WineCatalog.getInstance();
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

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void mainLoop() throws ClassNotFoundException, IOException {
		// Run main command execution logic

		while (this.socket.isConnected()) {

			System.out.println("Waiting for commands...");

			String command = (String) inStream.readObject();
			
			System.out.println(command);
			
			switch (command) {
			
			case "add":
				AddHandler.getInstance().run(inStream, outStream, loggedUser);
			case "sell":
				SellHandler.getInstance().run(inStream, outStream, loggedUser);
			case "view":
				ViewHandler.getInstance().run(inStream, outStream, loggedUser);
			case "buy":
				BuyHandler.getInstance().run(inStream, outStream, loggedUser);
			case "wallet":
				WalletHandler.getInstance().run(inStream, outStream, loggedUser);
			case "classify":
				ClassifyHandler.getInstance().run(inStream, outStream, loggedUser);
			case "talk":
				TalkHandler.getInstance().run(inStream, outStream, loggedUser);
			case "read":
				ReadHandler.getInstance().run(inStream, outStream, loggedUser);

			default:
				
				//Default case for wrong command message
				
				break;
			}

		}

	}
}
