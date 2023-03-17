package domain;

import java.util.Stack;

/**
 * The User class represents the user in this application.
 * Each user has a password, an id (username), a balance and a mailBox 
 * with all the unread messages
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class User {
	
	private String id;
	private double balance;
	private String password;
	private Stack<Message> inbox;
	
	/**
	 * Creates a new user with the given id and password.
	 * 
	 * @param id	The username	
	 * @param pwd	The password
	 */
	public User(String id, String pwd) {
		this.id = id;
		this.balance = 200;
		this.password = pwd;
		inbox = new Stack<>();
	}
	
	/**
	 * Returns the id of this user
	 * 
	 * @return	The id of this user
	 */
	public String getID() {
		return this.id;
	}
	
	/**
	 * Returns the password of this user
	 * 
	 * @return	The password of this user
	 */
	public String getPassword() {
		return this.password;
	}
	
	/**
	 * Returns the balance of this user
	 * 
	 * @return	The balance of this user
	 */
	public synchronized double getBalance() {
		return this.balance;
	}
	
	/**
	 * Sets balance to a new value
	 * 
	 * @param value		The new value
	 */
	public synchronized void setBalance(double value) {
		this.balance = value;
	}
	
	/**
	 * Returns the mailBox of this user
	 * 
	 * @return	A stack with all unread messages of this user
	 */
	public synchronized Stack<Message> getInbox() {
		return inbox;
	}
	
	/**
	 * Adds a new message to the mailBox of this user
	 * 
	 * @param msg	The message to add
	 */
	public synchronized void addMessage(Message msg) {
		inbox.push(msg);
	}
}
