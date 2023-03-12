package Client;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import domain.Message;
import domain.Wine;

public class TintoIMarket {
	
	public static final String COMMAND_INSTRUCTIONS =
			"To use the application, use the commands below (either full command word or first letter): \n" +
			"\n" +
			"add <wine> <image> 		- add a new wine identified by <wine> and associated to the image <image> \n" +
			"sell <wine> <value> <quantity>  - put up for sell <quantity> units of wine with the price <value> \n" +
			"view <wine> 			- get the <wine> information \n" +
			"buy <wine> <seller> <quantity>  - buy <quantity> units of wine to user <seller> \n" +
			"wallet 				- get the current balance \n" +
			"classify <wine> <stars>		- assign the classification <stars> (1 to 5) to wine <wine> \n" +
			"talk <user> <message>		- send a private message <message> to user <user> \n" +
			"read				- read new received messages \n" +
			"\n" +
			"Your command: ";
	
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
			
			System.out.println("Cliente: " + clientID + " Password: " + password + " ipPort: " + address);
			
			if(sessionHandler.getSessionValid()) {
				runClient(sessionHandler);
			} else {
				System.out.println("User or password incorrect");
			}
			
			
			
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Missing arguments. Correct usage: ...");
		}
	
	}

	private static void runClient(SessionHandler sessionHandler) {
		System.out.println("Welcome to TintoIMarket!");
		Scanner sc = new Scanner(System.in);
		
		while (true) {
			System.out.print(COMMAND_INSTRUCTIONS);
			if(sc.hasNext()) {
				
				String line = sc.nextLine();
				
				Pattern pattern = Pattern.compile(":");
				Matcher matcher = pattern.matcher(line);
				
				if (!matcher.find()) {
					String[] command = line.split(" ");
					System.out.println(sessionHandler.processCommand(command));
				}
				else {
					System.out.println("Don't use colon (:). Try again \n");
				}
			}
		}
	}
}
