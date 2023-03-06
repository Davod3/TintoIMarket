package Server;

public class TintoIMarketServer {

	public static void main(String[] args) {
		
		int port = 1234;
		
		if(args.length >= 1){
			
			//Set port
			port = Integer.parseInt(args[0]);
			
		}
		
		//Create Server
		Server myServer = new Server(port);
		myServer.run();

	}
}
