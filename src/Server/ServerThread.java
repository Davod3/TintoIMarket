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
				
				System.out.println("Gets here 1");
				String wine = (String) inStream.readObject();
				System.out.println("Gets here 2");
				File received = receiveFile();
				
				System.out.println("gets here 5");
				
				boolean result = wineCatalog.createWine(wine, received, loggedUser);
				
				System.out.println("gets here 6 " + result);
				
				if(result) {
					outStream.writeObject("Wine " + wine + " succesfully registered!");
				} else {
					outStream.writeObject("Failed to add wine. " + wine + " already exists.");
				}
				
				break;
			
			case "sell":
				
				break;

			case "view":
				
				break;
			
			case "buy":
				
				break;
			
			case "wallet":
				
				break;
			
			case "classify":
				
				break;
				
			case "talk":
				
				break;
			
			case "read":
				
				break;
				
			default:
				
				//Default case for wrong command message
				
				break;
			}

		}

	}

	private File receiveFile() throws ClassNotFoundException, IOException {
		
		System.out.println("gets here 3");
		
		String name = (String) inStream.readObject();
		int size = (int) inStream.readObject();
		
		System.out.println(name);
		System.out.println(size);
		
		byte[] bytes = new byte[size];
		
		System.out.println("gets here 3.5");
		
		inStream.read(bytes);
		//inStream.read(bytes, 0, size); //Error
		
		System.out.println("gets here 3.6");
		
		File outFile = new File(name);
		
		System.out.println("gets here 3.7");
		
		FileOutputStream fout = new FileOutputStream(outFile);
		
		System.out.println("gets here 3.8");
		fout.write(bytes);
		
		System.out.println("gets here 3.9");
		
		fout.close();
		
		System.out.println("Gets here 4");
		
		return outFile;
	}

}
