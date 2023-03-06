package Client;

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
	
	public boolean getSessionValid() {
		return sessionValid;
	}
}
