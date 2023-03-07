package Catalogs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Elements.User;

public class UserCatalog {

	private static UserCatalog instance = null;
	private Map<String, User> userList;
	
	private static final String USER_FILE_PATH = "users.txt";
	private static final String EOL = System.lineSeparator();

	private UserCatalog() throws IOException {

		this.userList = loadUsers();
		
	}

	private Map<String, User> loadUsers() throws IOException {
		
		Map<String, User> users = new HashMap<String, User>();
		
		BufferedReader br = new BufferedReader(new FileReader(USER_FILE_PATH));
		
		String line;
		
		while((line = br.readLine()) != null) {
			
			String[] splitData = line.split(":");
			
			users.put(splitData[0], new User(splitData[0], splitData[1]));
			
		}
		
		br.close();
		
		return users;
		
	}

	public static UserCatalog getInstance() throws IOException {

		if (instance == null)
			instance = new UserCatalog();
		return instance;

	}

	public User validate(String userId, String pwd) throws IOException {

		User loggedUser;

		if (this.userList.containsKey(userId)) {
			// Check if pwd is valid
			loggedUser = this.userList.get(userId);

			if (!pwd.equals(loggedUser.getPassword())) {
				//Validation failed
				loggedUser = null;
			}
		} else {
			
			//New user, register it
			loggedUser = registerUser(userId, pwd);
			

		}

		return loggedUser;

	}

	public User registerUser(String userId, String pwd) throws IOException {
		
		User registering = new User(userId, pwd);
		
		userList.put(userId, registering);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE_PATH, true));
		String entry = EOL + userId + ":" + pwd;
		bw.append(entry);
		bw.close();
		
		return registering;
		
	}

}
