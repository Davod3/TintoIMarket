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
	
	public void add(Wine wine, String imageFile) {
		
	}
	
	public void sell(Wine wine, double value, int quantity) {
		
	}
	
	public void view(Wine wine) {
		
	}
	
	public void buy(Wine wine, User seller, int quantity) {
		
	}
	
	public void wallet() {
		
	}
	
	public void classify(Wine wine, int starts) {
		
	}
	
	public void talk(User user, Message message) {
		
	}
	
	public void read() {
		
	}
	
	public boolean getSessionValid() {
		return sessionValid;
	}
}
