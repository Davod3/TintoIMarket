package Elements;

public class User {
	
	private String id;
	private int balance;
	private String password;
	
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
	
	public int getBalance() {
		return this.balance;
	}
	
}
