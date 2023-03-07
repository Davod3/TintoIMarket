package Client;

public class TintoIMarket {
	
	public static void main(String[] args) {
		String address = "";
		String clientID ="";
		String password = "";
		SessionHandler sessionHandler = null;
		try {
			address = args[0];
			clientID = args[1];
			password = args[2];
			sessionHandler = new SessionHandler(clientID, password, address);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Missing 1 argument. Try again");
		}
		System.out.println("Cliente: " + clientID + " Password: " + password + " ipPort: " + address);
		
		if(sessionHandler.getSessionValid()) 
			runClient();
		else
			System.out.println("User or password incorrect");
	}
	
	public static void runClient() {
		System.out.print("Type a command:");
		while(true) {
			
		}
	}
}
