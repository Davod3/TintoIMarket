package server;

/**
 * This class represents the threads of the server of this application
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class TintoIMarketServer {

	/**
	 * Starts the server with the given IP address and Port number
	 * 
	 * @param args		The address of the server
	 */
	public static void main(String[] args) {
		//Default port for connection
		int port = 12345;
		//Check if there is any port number for the connection
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
