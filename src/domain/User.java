package domain;

import java.util.List;

public class User {
	
	private String id;
	private double balance;
	private String password;
	private List<Message> messages;
	
	public User(String id, String pwd) {
		
		this.id = id;
		this.balance = 200;
		this.password = pwd;
		
	}
	
	public String getID() {
		return this.id;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public double getBalance() {
		return this.balance;
	}
	
	public void setBalance(double d) {
		this.balance = d;
	}
	
}
