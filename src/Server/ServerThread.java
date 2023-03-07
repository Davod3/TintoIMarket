package Server;

import java.io.File;
import java.io.FileOutputStream;
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

			System.out.println(inStream.available());

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
			
			switch (command) {
			
			case "add":
				
				String wine = (String) inStream.readObject();
				File received = receiveFile();
				
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
		
		String name = (String) inStream.readObject();
		int size = (Integer) inStream.readObject();
		
		byte[] bytes = new byte[size];
		
		inStream.read(bytes, 0, size);
		
		File outFile = new File("Recebido.txt");
		
		FileOutputStream fout = new FileOutputStream(outFile);
		fout.write(bytes);
		
		return outFile;
	}

}
