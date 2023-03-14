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
		inbox = new Stack<Message>();
	}
	
	public String getID() {
		return this.id;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public synchronized double getBalance() {
		return this.balance;
	}
	
	public synchronized void setBalance(double d) {
		this.balance = d;
	}
	
	public synchronized Stack<Message> getInbox() {
		return inbox;
	}
	
	public synchronized void addMessage(Message msg) {
		inbox.push(msg);
	}
}
