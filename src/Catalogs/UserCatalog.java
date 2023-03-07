package Catalogs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Elements.User;

public class UserCatalog {
	
	private static UserCatalog instance = null;
	private Map<String, User> userList;
	
	private UserCatalog() {
		
		this.userList = new HashMap<String, User>();
		
	}
	
	public static UserCatalog getInstance() {
		
		if(instance == null)
			instance = new UserCatalog();
		return instance;

	}

	public boolean validate(String userId, String pwd) {
		
		return true;
	}
	
}
