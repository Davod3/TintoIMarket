package client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents the client of this application.
 * 
 * @author André Dias nº 55314
 * @author David Pereira nº 56361
 * @author Miguel Cut nº 56339
 */
public class TintolMarket {

	private static Scanner sc = new Scanner(System.in);
	public static final String COMMAND_INSTRUCTIONS = "To use the application, use the commands below (either full command word or first letter): \n"
			+ "\n"
			+ "add <wine> <image> 		- add a new wine identified by <wine> and associated to the image <image> \n"
			+ "sell <wine> <value> <quantity>  - put up for sell <quantity> units of wine with the price <value> \n"
			+ "view <wine> 			- get the <wine> information \n"
			+ "buy <wine> <seller> <quantity>  - buy <quantity> units of wine to user <seller> \n"
			+ "wallet 				- get the current balance \n"
			+ "classify <wine> <stars>		- assign the classification <stars> (1 to 5) to wine <wine> \n"
			+ "talk <user> <message>		- send a private message <message> to user <user> \n"
			+ "read				- read new received messages \n \n";

	/**
	 * Executes the client
	 * 
	 * @param args ServerAddress, clientID and password
	 */
	public static void main(String[] args) {
		String address = "";
		String clientID = "";
		String password = "";
		SessionHandler sessionHandler = null;

		try {
			// Get client arguments
			if (args.length != 3) {
				password = getPassword();
			} else {
				password = args[2];
			}
			address = args[0];
			clientID = args[1];

			System.out.println("Cliente: " + clientID + " Password: " + password + " ipPort: " + address);

			while (!(sessionHandler = new SessionHandler(clientID, password, address)).getSessionValid()) {
				System.out.println("Incorrect user or password");
				password = getPassword();
			}
			runClient(sessionHandler);

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Missing arguments. Correct usage: <serverAddress> <userID> <password>");
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println("Unknown host. Wrong IP or Port used.");
		}
	}

	/**
	 * Reads the password from the user
	 * 
	 * @return User's password
	 */
	public static String getPassword() {
		String password = "";
		boolean gotPassword = false;
		while (!gotPassword) {
			System.out.print("Write your password again: ");
			password = sc.nextLine();
			if (password != null && !password.equals("")) {
				gotPassword = true;
			}
		}
		return password;
	}

	/**
	 * Runs the client's engine
	 * 
	 * @param sessionHandler The handler for this session/connection
	 */
	private static void runClient(SessionHandler sessionHandler) {
		System.out.println("Welcome to TintoIMarket!");
		System.out.print(COMMAND_INSTRUCTIONS);
		boolean help = false;
		System.out.print("Your command: ");

		while (true) {

			if (help) {
				System.out.println();
				System.out.println("Type help to see commands");
				System.out.print("Your command: ");
			} else {
				help = true;
			}

			if (sc.hasNext()) {

				String line = sc.nextLine();
				System.out.println();
				if (line.equals("help")) {
					System.out.print(COMMAND_INSTRUCTIONS);
				} else {
					Pattern pattern = Pattern.compile(":");
					Matcher matcher = pattern.matcher(line);

					if (!matcher.find()) {
						String[] command = line.split(" ");
						System.out.println(sessionHandler.processCommand(command));
					} else {
						System.out.println("Don't use colon (:). Try again \n");
					}
				}
			}
		}
	}
}
