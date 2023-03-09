package Catalogs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import domain.Message;
import domain.User;

public class UserCatalog {

	private static UserCatalog instance = null;
	private Map<String, User> userList;
	
	private static final String USER_FILE_PATH = "users.txt";
	private static final String EOL = System.lineSeparator();
	private static final String SEPARATOR = ":";

	private UserCatalog() throws IOException {

		this.userList = loadUsers();
		
	}

	private Map<String, User> loadUsers() throws IOException {
		
		Map<String, User> users = new HashMap<String, User>();
		
		File userFile = new File(USER_FILE_PATH);
		userFile.createNewFile(); //Make sure file exists before reading
		
		BufferedReader br = new BufferedReader(new FileReader(USER_FILE_PATH));
		
		String line;
		
		while((line = br.readLine()) != null) {
			
			String[] splitData = line.split(SEPARATOR);
			
			if( splitData.length >= 2) {
				
				users.put(splitData[0], new User(splitData[0], splitData[1]));
				
			}
			
		}
		
		br.close();
		
		return users;
		
	}

	public boolean hasEnoughMoney(String user, double value) {
		return getUser(user).getBalance() >= value;
	}
	
	public void transfer(String buyer, String seller, double value) {
		User buyerUser = getUser(buyer);
		User sellerUser = getUser(seller);
		buyerUser.setBalance(buyerUser.getBalance() - value);
		sellerUser.setBalance(sellerUser.getBalance() + value);
	}
	
	public static UserCatalog getInstance() throws IOException {

		if (instance == null)
			instance = new UserCatalog();
		return instance;

	}

	public String validate(String userId, String pwd) throws IOException {

		String loggedUser = null;

		if (this.userList.containsKey(userId)) {
			// Check if pwd is valid
			User user = this.userList.get(userId);

			if (pwd.equals(user.getPassword())) {
				//Validation failed
				loggedUser = userId;
			}
					
		} else {
			
			//New user, register it
			loggedUser = registerUser(userId, pwd);
			

		}

		return loggedUser;

	}

	public String registerUser(String userId, String pwd) throws IOException {
		
		User registering = new User(userId, pwd);
		
		userList.put(userId, registering);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE_PATH, true));
		String entry = EOL + userId + SEPARATOR + pwd;
		bw.append(entry);
		bw.close();
		
		return userId;
		
	}
	
	public String readMessages(String loggedUser) {
		User user = getUser(loggedUser);
		StringBuilder sb = new StringBuilder();
		Stack<Message> userInbox = user.getInbox();
		sb.append("You have: " + userInbox.size() + " new messages" + EOL);
		while(!userInbox.isEmpty()) {
			Message message = userInbox.pop();
			sb.append("-->From: " + message.getFrom() + "; Message: " + message.getContent());
		}
		return sb.toString();
	}
	
	public User getUser(String user) {
		return userList.get(user);
	}
}
