package server;

public class TintoIMarketServer {

	public static void main(String[] args) {
		
		int port = 12345;
		
		if(args.length >= 1){
			
			//Set port
			try {
				
				port = Integer.parseInt(args[0]);
				
			} catch (Exception e) {
				
				System.out.println("Input argument must be a number. Using default port.");
				
			}
			
		}
		
		//Create Server
		Server myServer = new Server(port);
		myServer.run();

	}
}
