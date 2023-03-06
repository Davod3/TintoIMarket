package Client;

public class SessionHandler {
	
	private String user;
	private String password;
	private boolean sessionValid;
	private NetworkClient netClient;
	
	public SessionHandler(String user, String password, NetworkClient netClient) {
		this.user = user;
		this.password = password;
		this.netClient = netClient;
		sessionValid = netClient.validateSession(user, password);
	}
	
	public boolean GetSessionValid() {
		return sessionValid;
	}
}
