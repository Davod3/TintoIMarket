package Client;

import domain.Message;
import domain.User;
import domain.Wine;

public class SessionHandler {
	
	private String user;
	private String password;
	private boolean sessionValid;
	private NetworkClient netClient;
	
	public SessionHandler(String user, String password, String serverAdress) {
		this.user = user;
		this.password = password;
		this.netClient = new NetworkClient(serverAdress);
		
		sessionValid = netClient.validateSession(user, password);
	}
	
	public void add(String wine, String imageFile) {
		System.out.println("add");
	}
	
	public void sell(String wine, String value, String quantity) {
		System.out.println("sell");

	}
	
	public void view(String wine) {
		System.out.println("view");

	}
	
	public void buy(String wine, String seller, String quantity) {
		System.out.println("buy");

	}
	
	public void wallet() {
		System.out.println("wallet");

	}
	
	public void classify(String wine, String starts) {
		System.out.println("classify");

	}
	
	public void talk(String user, String message) {
		System.out.println("talk");

	}
	
	public void read() {
		System.out.println("read");

	}
	
	public boolean getSessionValid() {
		return sessionValid;
	}
}
