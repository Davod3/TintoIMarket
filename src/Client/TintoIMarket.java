package Client;

import java.util.Scanner;

import domain.Message;
import domain.Wine;

public class TintoIMarket {
	
	public static final String COMMAND_INSTRUCTIONS =
			"To use the application, use the comands below: \n" +
			"add <wine> <image> - adds a new wine identified by <wine> and associated to the image <image> \n" +
			"sell <wine> <value> <quantity> - puts ";
	
	public static void main(String[] args) {
		String address = "";
		String clientID ="";
		String password = "";
		SessionHandler sessionHandler = null;
		
		try {
			address = args[0];
			clientID = args[1];
			password = args[2];
			sessionHandler = new SessionHandler(clientID, password, address);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Missing 1 argument. Try again");
		}
		System.out.println("Cliente: " + clientID + " Password: " + password + " ipPort: " + address);
		
		if(sessionHandler.getSessionValid()) {
			runClient(sessionHandler);
		} else {
			System.out.println("User or password incorrect");
		}
	}

	private static void runClient(SessionHandler sessionHandler) {
		System.out.println("Welcome to TintoIMarket!");
		Scanner sc = new Scanner(System.in);
		
		while (true) {
			System.out.println(COMMAND_INSTRUCTIONS);
			if(sc.hasNext()) {
				String[] command = sc.nextLine().split(" ");
				sessionHandler.processCommand(command);
			}
		}
	}
}
