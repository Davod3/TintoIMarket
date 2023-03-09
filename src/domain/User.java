package domain;

import java.util.Stack;

public class User {
	
	private String id;
	private double balance;
	private String password;
	private Stack<Message> inbox;
	
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
	
	public Stack<Message> getInbox() {
		return inbox;
	}
	
	public void addMessage(Message msg) {
		inbox.push(msg);
	}
}
