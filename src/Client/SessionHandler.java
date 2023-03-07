package Client;

import java.util.Scanner;

import domain.Message;
import domain.User;
import domain.Wine;

public class SessionHandler {
	
	private String user;
	private String password;
	private boolean sessionValid;
	private NetworkClient netClient;
	public static final String COMMAND_ERROR = "Invalid command.";
	
	public SessionHandler(String user, String password, String serverAdress) {
		this.user = user;
		this.password = password;
		this.netClient = new NetworkClient(serverAdress);
		
		sessionValid = netClient.validateSession(user, password);
	}
	
	public boolean getSessionValid() {
		return sessionValid;
	}
	
	public void processCommand(String[] command) {
		
		switch (command.length) {
		case 1:
			if (command[0].equals("read") || command[0].equals("r")) {
				netClient.read();
			}
			else if (command[0].equals("wallet") || command[0].equals("w")) {
				netClient.wallet();
			}
			else {
				System.out.println(COMMAND_ERROR);
			}
			break;
		case 2:
			if (command[0].equals("view") || command[0].equals("v") ) {
				netClient.view(command[1]);
			}
			else {
				System.out.println(COMMAND_ERROR);
			}
			break;
		case 3:
			if (command[0].equals("add") || command[0].equals("a")) {
				netClient.add(command[1], command[2]);
			}
			else if (command[0].equals("classify") || command[0].equals("c")) {
				netClient.classify(command[1], command[2]);
			}
			else if (command[0].equals("talk") || command[0].equals("t")) {
				netClient.talk(user, command[1], command[2]);
			}
			else {
				System.out.println(COMMAND_ERROR);
			}
			break;
		case 4:
			if (command[0].equals("buy") || command[0].equals("b")) {
				netClient.buy(command[1], command[2], command[3]);
			}
			else if (command[0].equals("sell") || command[0].equals("s")) {
				netClient.sell(command[1], command[2], command[3]);
			}
			else {
				System.out.println(COMMAND_ERROR);
			}
			break;
		default:
			System.out.println(COMMAND_ERROR);
			break;
		}
			}
		}
