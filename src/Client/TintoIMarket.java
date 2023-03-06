package Client;

public class TintoIMarket {
	
	private String clientID = "";
	private String password = "";
	private String ipPort = "";
	
	private TintoIMarket(String[] args) {
		try {
			this.ipPort = args[0];
			this.clientID = args[1];
			this.password = args[2];
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Missing 1 argument. Try again");
		}
	}
	
	public static void main(String[] args) {
		TintoIMarket client = new TintoIMarket(args);
		
		System.out.println("Cliente: " + client.clientID + " Password: " + client.password + " ipPort: " + client.ipPort);
		
		
	}
	
}
