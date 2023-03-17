package catalogs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import domain.Message;
import domain.User;
import utils.FileUtils;

/**
 * The UserCatalog class represents the catalog with all users
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class UserCatalog {

	private static UserCatalog instance = null;
	private Map<String, User> userList;
	
	private static final String USER_FILE_PATH = "users.txt";
	private static final String USER_MESSAGES_PATH = "users/";
	private static final String USER_MESSAGES_EXTENSION = ".txt";
	private static final String SEPARATOR = ":";

	/**
	 * Creates a UserCatalog and loads all users from a users file
	 * 
	 * @throws IOException	When an I/O error occurs while loading all users
	 */
	private UserCatalog() throws IOException {
		this.userList = loadUsers();
	}

	/**
	 * Returns a map with all users, after loading their unread messages
	 * 
	 * @return					A map containing all users
	 * @throws IOException		When an I/O error occurs while reading from the file
	 */
	private synchronized Map<String, User> loadUsers() throws IOException {
		//Create a map to load users
		Map<String, User> users = new HashMap<String, User>();
		//Open the file that contains all the users information
		File userFile = new File(USER_FILE_PATH);
		userFile.createNewFile(); //Make sure file exists before reading
		//Open a reader to read from the opened file
		BufferedReader br = new BufferedReader(new FileReader(USER_FILE_PATH));
		//Read each line of the file
		String line;
		while((line = br.readLine()) != null) {
			//Split the data
			String[] splitData = line.split(SEPARATOR);
			if (splitData.length >= 2) {
				//Create new user with the given name and password
				User user = new User(splitData[0], splitData[1]);
				//Load all unread messages of that user
				loadMessages(user);
				//Load user in map
				users.put(splitData[0], user);	
			}
		}
		br.close();
		return users;
	}

	/**
	 * Loads the unread messages of the given user, which are in a specific file
	 * 
	 * @param user				The user for who we want to get the unread messages
	 * @throws IOException		When an I/O error occurs while reading from a file
	 */
	private void loadMessages(User user) throws IOException {
		//Create the user path
		String filePath = USER_MESSAGES_PATH + user.getID() + USER_MESSAGES_EXTENSION;
		//Get the file with the specific user path
		File messageDir = new File(filePath);
		messageDir.getParentFile().mkdirs(); //Create parent directory if non existent
		messageDir.createNewFile(); //Create file before reading
		//Open a reader to read the messages from the file
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		//Read messages from the file
		String line;
		while((line = br.readLine()) != null) {
			//from:to:content
			//Split the data
			String[] splitData = line.split(SEPARATOR);
			//Create a new message with the given data
			Message message = new Message(splitData[0], splitData[1], splitData[2]);
			//Add the message to the database
			user.addMessage(message);	
		}
		br.close();
	}

	/**
	 * Checks if a user has enough money
	 * 
	 * @param user		The user we want to see if has enough money
	 * @param value		The value to compare
	 * @return			True if user has balance greater than or equal to value,
	 * 					false otherwise
	 */
	public boolean hasEnoughMoney(String user, double value) {
		return getUser(user).getBalance() >= value;
	}
	
	/**
	 * Transfers a certain amount of money from the buyer to the seller
	 * 
	 * @param buyer		The user from which we want to transfer the value
	 * @param seller	The user to which we want to transfer the value
	 * @param value		The value we want to transfer
	 */
	public void transfer(String buyer, String seller, double value) {
		User buyerUser = getUser(buyer);
		User sellerUser = getUser(seller);
		buyerUser.setBalance(buyerUser.getBalance() - value);
		sellerUser.setBalance(sellerUser.getBalance() + value);
	}
	
	
	/**
	 * Returns the unique instance of the UserCatalog class.
	 * If there is no instance of the class, a new one is created and returned.
	 * 
	 * @return					The unique instance of the UserCatalog class
	 * @throws IOException		When an I/O error occurs while reading from a file
	 */
	public static UserCatalog getInstance() throws IOException {
		if (instance == null)
			instance = new UserCatalog();
		return instance;
	}

	/**
	 * Returns the loggedUser username, after trying to log him in,
	 * or after creating a new account for the given user.
	 * If the username and password match, returns the username.
	 * If the username does not exist, creates a new account and
	 * returns the given username.
	 * 
	 * @param userId			Username
	 * @param pwd				User password
	 * @return					The loggedUser username
	 * @throws IOException		When an I/O error occurs
	 * 							while reading from a file
	 */
	public synchronized String validate(String userId, String pwd)
			throws IOException {
		//Create the result string (username)
		String loggedUser = null;
		//Check if userId is in UserCatalog
		if (this.userList.containsKey(userId)) {
			// Check if password is valid
			User user = this.userList.get(userId);
			if (pwd.equals(user.getPassword())) {
				//Validation succeeded 
				loggedUser = userId;
			}		
		} else {	
			//New user, register it
			loggedUser = registerUser(userId, pwd);
		}
		return loggedUser;
	}

	/**
	 * Registers a new user given the username and password
	 * 
	 * @param userId			Username
	 * @param pwd				Password
	 * @return					Username
	 * @throws IOException		When an I/O error occurs while
	 * 							reading/writing to a file
	 */
	public synchronized String registerUser(String userId, String pwd)
			throws IOException {
		//Create new user
		User registering = new User(userId, pwd);
		//Insert new user in dataBase
		userList.put(userId, registering);
		//Open reader to write to file
		BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE_PATH, true));
		//Create string with user and password
		String entry = FileUtils.EOL + userId + SEPARATOR + pwd;
		//Write string to file
		bw.append(entry);
		bw.close();
		return userId;	
	}
	
	/**
	 * Gets all unread messages from the given user
	 * 
	 * @param loggedUser		The user for which we want to get the messages
	 * @return					The unread messages
	 * @throws IOException		When an I/O error occurs while
	 * 							reading/writing to a file
	 */
	public String readMessages(String loggedUser) throws IOException {
		//Get user by username
		User user = getUser(loggedUser);
		//Open a StringBuilder to write all messages
		StringBuilder sb = new StringBuilder();
		//Get all unread messages
		Stack<Message> userInbox = user.getInbox();
		//Write the numbers of unread messages the user has
		sb.append("You have: " + userInbox.size() + " new messages" + FileUtils.EOL);
		//Write all unread messages
		while(!userInbox.isEmpty()) {
			Message message = userInbox.pop();
			sb.append("-->From: " + message.getFrom()
				+ "; Message: " + message.getContent() + FileUtils.EOL);
		}
		updateMessages(user);
		return sb.toString();
	}
	
	/**
	 * Returns the user with the given username
	 * 
	 * @param user		The user to look for
	 * @return			The user with the given username
	 */
	public synchronized User getUser(String user) {
		return userList.get(user);
	}

	/**
	 * Checks if user with username exists in database
	 * 
	 * @param user		The user to check
	 * @return			True if user exists in database,
	 * 					false otherwise
	 */
	public synchronized boolean exists(String user) {
		return this.userList.containsKey(user);
	}

	/**
	 * Adds a new message to the mailBox of the given user
	 * 
	 * @param username			The user for which we want to send a new message
	 * @param msgToSend			The message to send
	 * @throws IOException		When an I/O error occurs while reading/writing to a file
	 */
	public synchronized void addMessageToUser(String username, Message msgToSend)
			throws IOException {
		//Get user by username
		User user = userList.get(username);
		//Add message to user
		user.addMessage(msgToSend);
		updateMessages(user);	
	}

	/**
	 * Updates all unread messages to user file
	 * 
	 * @param user				The user for which we want to update all unread messages
	 * @throws IOException		When an I/O error occurs while reading/writing to a file
	 */
	private synchronized void updateMessages(User user) throws IOException {
		//Get path to file with all unread messages
		String filePath = USER_MESSAGES_PATH + user.getID() + USER_MESSAGES_EXTENSION;
		//Get file with the specified path
		File messageDir = new File(filePath);
		messageDir.getParentFile().mkdirs(); //Create parent directory if non existent
		messageDir.createNewFile(); //Create file before reading
		//Open reader to read from file
		BufferedWriter bf = new BufferedWriter(new FileWriter(filePath));
		//Get all unread messages
		List<Message> messages = user.getInbox();
		//Write all unread messages
		for(Message msg : messages) {
			bf.append(msg.getFrom() + SEPARATOR + msg.getTo() + SEPARATOR + msg.getContent() + FileUtils.EOL);
		}
		bf.close();	
	}
}
