package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import utils.FileUtils;

/**
 * This class represents the network for the client of this application.
 * It communicates directly with the server through sockets opened early.
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class NetworkClient {

	private Socket clientSocket;
	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;
	
	private static final String DEFAULT_PORT = "12345";
	private static final String TRUSTSTORE_PWD = "keystorepwd";
	private static final String KEY_ALIAS = "client";
		
	/**
	 * Creates a new network for the client
	 * 
	 * @param serverAddress				The address of the server to connect to
	 * @throws IOException				When an I/O error occurs while 
	 * 									reading/writing to a file
	 */
	public NetworkClient(String serverAddress, String truststore) throws IOException {
		//Check if address contains port, otherwise use default
		String[] addressSplit;
		if(serverAddress.contains(":")) {
			addressSplit = serverAddress.split(":");
		} else {
			//Use default port
			addressSplit = new String[2];
			addressSplit[0] = serverAddress;
			addressSplit[1] = DEFAULT_PORT;
		}
		
		System.setProperty("javax.net.ssl.trustStore", truststore);
		System.setProperty("javax.net.ssl.trustStorePassword", TRUSTSTORE_PWD);
		
		//Get address
		String host = addressSplit[0];
		int port = Integer.parseInt(addressSplit[1]);
		
		//Connect to server
		SocketFactory sf = SSLSocketFactory.getDefault();
		clientSocket = sf.createSocket(host, port);

		createStreams();
	}

	/**
	 * Creates the streams for further communication between client and server
	 */
	private void createStreams() {
		try {
			inStream = new ObjectInputStream(clientSocket.getInputStream());
			outStream = new ObjectOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Erro a criar streams");
		}
	}
	
	/**
	 * Validates the session of the current user
	 * 
	 * @param user			The current user
	 * @param password		User's password
	 * @return				True if user can log in, false otherwise
	 */
	public boolean validateSession(KeyStore keystore, String keystorePassword, String user) {
		boolean validation = false, knownUser = false;
		long nonce;
		try {
			//Send user
			outStream.writeObject(user);
			
			nonce = (long) inStream.readObject();
			knownUser = (boolean) inStream.readObject();
			PrivateKey pk = (PrivateKey) keystore.getKey(KEY_ALIAS, keystorePassword.toCharArray());
			SignedObject signedNonce = new SignedObject(nonce, pk, Signature.getInstance("MD5withRSA"));
			
			//Send signed nonce either way
			outStream.writeObject(signedNonce);
			
			//If user is unknown send certificate
			if(!knownUser) { 
				Certificate certs[] = keystore.getCertificateChain(KEY_ALIAS);
				outStream.writeObject(certs[0]);
			}
			
			validation = (boolean) inStream.readObject();
		} catch (IOException e) {
			System.out.println("Erro ao enviar user e password para a socket");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return validation;
	}
	
	/**
	 * Adds a new wine to the market by sending the add command to the server
	 * 
	 * @param wine							The name of the wine
	 * @param imageFile						The path to the image of the wine
	 * @return								A message confirming that the wine was 
	 * 										successfully added to the market
	 * @throws IOException					When inStream does not receive input
	 * 										or the outStream can't send a message
	 * @throws ClassNotFoundException		When trying to find the class of an object
	 * 										that does not match/exist
	 */
	public String add(String wine, String imageFile)
			throws IOException, ClassNotFoundException {
		String result = "";
		//Send add command
		outStream.writeObject("add");
		//Send the name of the wine
		outStream.writeObject(wine);
		//Send image of the wine
		FileUtils.sendFile(imageFile, outStream);
		result = (String) inStream.readObject();
		return result;
	}
	
	/**
	 * Sells the given units of wine for the given price by sending
	 * the sell command to the server
	 * 
	 * @param wine							The name of the wine
	 * @param value							The price of each unit
	 * @param quantity						The quantity of wine to sell
	 * @return								A message confirming that the wine was 
	 * 										successfully put on sale
	 * @throws IOException					When inStream does not receive input
	 * 										or the outStream can't send a message
	 * @throws ClassNotFoundException		When trying to find the class of an object
	 * 										that does not match/exist
	 */
	public String sell(String wine, String value, String quantity)
			throws IOException, ClassNotFoundException {
		String result = "";
		//Send sell command
		outStream.writeObject("sell");
		//Send the name of the wine
		outStream.writeObject(wine);
		//Send value of each unit
		outStream.writeObject(Double.parseDouble(value));
		//Send quantity to sell
		outStream.writeObject(Integer.parseInt(quantity));
		//Get result
		result = (String) inStream.readObject();
		return result;
	}
	
	/**
	 * Views the given wine by sending the view command to the server
	 * 
	 * @param wine							The wine we want to view (information)
	 * @return								The information of the given wine
	 * @throws IOException					When inStream does not receive input
	 * 										or the outStream can't send a message
	 * @throws ClassNotFoundException		When trying to find the class of an object
	 * 										that does not match/exist
	 */
	public String view(String wine)
			throws IOException, ClassNotFoundException {
		String result = "";
		//Send view command
		outStream.writeObject("view");
		//Send the name of the wine
		outStream.writeObject(wine);
		//Check if wine exists
		boolean wineExists = inStream.readBoolean();
		//If wine exists receive image and information about wine
		if(wineExists)
			FileUtils.receiveFile(inStream);
		//Get result
		result = (String) inStream.readObject();
		return result;
	}
	
	/**
	 * Buys a certain quantity of wine to a specific seller by sending the buy command
	 * to the server
	 * 
	 * @param wine							The name of the wine to buy
	 * @param seller						The seller from whom you want to buy the wine
	 * @param quantity						The quantity you want to buy from the seller
	 * @return								A message confirming that the wine was 
	 * 										successfully bought
	 * @throws IOException					When inStream does not receive input
	 * 										or the outStream can't send a message
	 * @throws ClassNotFoundException		When trying to find the class of an object
	 * 										that does not match/exist
	 */
	public String buy(String wine, String seller, String quantity)
			throws IOException, ClassNotFoundException {
		String result = "";
		//Send buy command
		outStream.writeObject("buy");
		//Send the name of the wine
		outStream.writeObject(wine);
		//Send the seller
		outStream.writeObject(seller);
		//Send the quantity to buy
		outStream.writeObject(Integer.parseInt(quantity));
		//Get result
		result = (String) inStream.readObject();	 
		return result;
	}
	
	/**
	 * Get the actual balance of the current user by sending the wallet command
	 * to the server
	 * 
	 * @return								The actual balance of the current user
	 * @throws IOException					When inStream does not receive input
	 * 										or the outStream can't send a message
	 * @throws ClassNotFoundException		When trying to find the class of an object
	 * 										that does not match/exist	
	 */
	public String wallet() throws IOException, ClassNotFoundException {
		String result = "";
		//Send wallet command
		outStream.writeObject("wallet");
		//Get result
		result = (String) inStream.readObject();
		return result;
	}
	
	/**
	 * Rates a wine by sending the classify command to the server
	 * 
	 * @param wine							The wine to classify
	 * @param stars							The rating
	 * @return								A message confirming that 
	 * 										the wine was successfully rated
	 * @throws IOException					When inStream does not receive input
	 * 										or the outStream can't send a message
	 * @throws ClassNotFoundException		When trying to find the class of an object
	 * 										that does not match/exist	
	 */
	public String classify(String wine, String stars)
			throws IOException, ClassNotFoundException {
		String result = "";
		//Send classify command
		outStream.writeObject("classify");
		//Send the name of the wine
		outStream.writeObject(wine);
		//Send the rating
		outStream.writeObject(Integer.parseInt(stars));
		//Get result
		result = (String) inStream.readObject();
		return result;
	}
	
	/**
	 * Sends a message to a specific user by sending the talk command to the server
	 * 
	 * @param userTo						The user we want to send the message
	 * @param message						The message we want to send
	 * @return								A message confirming that the message was delivered
	 * @throws IOException					When inStream does not receive input
	 * 										or the outStream can't send a message
	 * @throws ClassNotFoundException		When trying to find the class of an object
	 * 										that does not match/exist	
	 */
	public String talk(String userTo, String message)
			throws IOException, ClassNotFoundException {
		String result = "";
		//Send talk command
		outStream.writeObject("talk");
		//Send the receiver of the message
		outStream.writeObject(userTo);
		//Send the message
		outStream.writeObject(message);
		//Get result
		result = (String) inStream.readObject();
		return result;
	}
	
	/**
	 * Gets all unread messages of the current user by sending the read command
	 * to the server
	 * 
	 * @return							All unread messages from the current user
	 * @throws ClassNotFoundException	When trying to find the class of an object
	 * 									that does not match/exist
	 * @throws IOException				When inStream does not receive input
	 * 									or the outStream can't send a message	
	 */
	public String read() throws ClassNotFoundException, IOException {
		String result = "";
		//Send read command
		outStream.writeObject("read");
		//Get result
		result = (String) inStream.readObject();
		return result;
	}
}
