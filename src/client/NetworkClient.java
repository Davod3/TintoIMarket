package client;

import java.io.File;
import java.io.FileInputStream;
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
import java.security.cert.CertificateException;
import java.util.Stack;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import domain.Message;
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
	private PrivateKey pk;
	private KeyStore truststore;
	private KeyStore keystore;
	private String truststorePath;
	private String keystorePwd;
	
	private static final String DEFAULT_PORT = "12345";
	private static final String TRUSTSTORE_PWD = "keystorepwd";
	private static final String KEY_ALIAS = "client";
	private static final String EOL = System.lineSeparator();
		
	/**
	 * Creates a new network for the client
	 * 
	 * @param serverAddress					The address of the server to connect to
	 * @throws IOException					When an I/O error occurs while 
	 * 										reading/writing to a file
	 * @throws CertificateException 		When an error occurs while generating
	 * 										the certificate from the fileInputStream
	 * @throws NoSuchAlgorithmException 	If the requested algorithm is not available
	 * @throws KeyStoreException 			If an exception occurs while accessing the keystore
	 */
	public NetworkClient(String serverAddress, String truststore,
			String keystore, String keystorepwd)
			throws IOException, KeyStoreException,
			NoSuchAlgorithmException,CertificateException {
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
		
		this.truststorePath = truststore;
		System.setProperty("javax.net.ssl.trustStore", truststore);
		System.setProperty("javax.net.ssl.trustStorePassword", TRUSTSTORE_PWD);
		
		//Get address
		String host = addressSplit[0];
		int port = Integer.parseInt(addressSplit[1]);
		
		//Connect to server
		SocketFactory sf = SSLSocketFactory.getDefault();
		clientSocket = sf.createSocket(host, port);
		
		this.truststore = getKeyStore(truststore, TRUSTSTORE_PWD.toCharArray());
		this.keystore = getKeyStore(keystore, keystorepwd.toCharArray());
		this.keystorePwd = keystorepwd;

		createStreams();
	}
	
	/**
	 * Gets the keystore given the file path for it
	 * 
	 * @param keystore						The file path for the KeyStore
	 * @param keystorepw					The password for the KeyStore
	 * @return								The KeyStore
	 * @throws KeyStoreException			If an exception occurs while accessing the keystore
	 * @throws NoSuchAlgorithmException		If the requested algorithm is not available
	 * @throws CertificateException			When an error occurs while generating the certificate
	 * 										from the fileInputStream
	 * @throws IOException					When an I/O error occurs while reading/writing to a file
	 */
	private KeyStore getKeyStore(String keystore, char[] keystorepw)
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException {
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		File ksFile = new File(keystore);
		FileInputStream fis = new FileInputStream(ksFile);
		ks.load(fis, keystorepw);
		
		return ks;
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
		boolean validation = false;
		boolean knownUser = false;
		long nonce;
		try {
			//Send user
			outStream.writeObject(user);
			
			nonce = (long) inStream.readObject();
			knownUser = (boolean) inStream.readObject();
			this.pk = (PrivateKey) keystore.getKey(KEY_ALIAS, keystorePassword.toCharArray());
			SignedObject signedNonce = new SignedObject(nonce, pk, Signature.getInstance("MD5withRSA"));
			
			//Send signed nonce either way
			outStream.writeObject(signedNonce);
			
			//If user is unknown send certificate
			if(!knownUser) { 
				Certificate[] certs = keystore.getCertificateChain(KEY_ALIAS);
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
	 * @throws NoSuchAlgorithmException 	If the requested algorithm is not available
	 * @throws SignatureException 			When an error occurs while signing an object
	 * @throws InvalidKeyException 			If the key is invalid
	 */
	public String sell(String wine, String value, String quantity)
			throws IOException, ClassNotFoundException, InvalidKeyException,
			SignatureException, NoSuchAlgorithmException {
		String result = "";
		//Send sell command
		outStream.writeObject("sell");
		//Send the name of the wine
		SignedObject signedWine = new SignedObject(wine, this.pk, Signature.getInstance("MD5withRSA"));
		outStream.writeObject(signedWine);
		//Send value of each unit
		SignedObject signedValue = new SignedObject(Double.parseDouble(value), this.pk, Signature.getInstance("MD5withRSA"));
		outStream.writeObject(signedValue);
		//Send quantity to sell
		SignedObject signedQuantity = new SignedObject(Integer.parseInt(quantity), this.pk, Signature.getInstance("MD5withRSA"));
		outStream.writeObject(signedQuantity);
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
	 * @throws NoSuchAlgorithmException 	If the requested algorithm is not available
	 * @throws SignatureException 			When an error occurs while signing an object
	 * @throws InvalidKeyException 			If the key is invalid
	 */
	public String buy(String wine, String seller, String quantity)
			throws IOException, ClassNotFoundException, InvalidKeyException,
			SignatureException, NoSuchAlgorithmException {
		String result = "";
		//Send buy command
		outStream.writeObject("buy");
		//Send the name of the wine
		SignedObject signedWine = new SignedObject(wine, this.pk, Signature.getInstance("MD5withRSA"));
		outStream.writeObject(signedWine);
		//Send the seller
		SignedObject signedSeller = new SignedObject(seller, this.pk, Signature.getInstance("MD5withRSA"));
		outStream.writeObject(signedSeller);
		//Send the quantity to buy
		SignedObject signedQuantity = new SignedObject(Integer.parseInt(quantity), this.pk, Signature.getInstance("MD5withRSA"));
		outStream.writeObject(signedQuantity);
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
	 * @throws BadPaddingException 			If the padding is invalid
	 * @throws IllegalBlockSizeException 	If the block size is invalid
	 * @throws NoSuchPaddingException 		If the padding scheme is not available
	 * @throws NoSuchAlgorithmException 	If the requested algorithm is not available
	 * @throws KeyStoreException 			If an exception occurs while accessing the keystore
	 * @throws InvalidKeyException 			If the key is invalid
	 * @throws CertificateException 		When an error occurs while generating the certificate
	 * 										from the fileInputStream
	 */
	public String talk(String userTo, String message)
			throws IOException, ClassNotFoundException, InvalidKeyException,
			KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, CertificateException {
		String result = "";
		
		//Encrypt the message
		AssimetricMessageEncryption ame = new AssimetricMessageEncryption(this.truststore, TRUSTSTORE_PWD, inStream, outStream, this.truststorePath);
		byte[] encryptedMsg = ame.encrypt(userTo, message);
		
		if(encryptedMsg.length <= 0) {
			
			return "Failed to encrypt message! User not found.";
		}
		
		//Send talk command
		outStream.writeObject("talk");
		//Send the receiver of the message
		outStream.writeObject(userTo);
		
		//Send the message
		outStream.writeObject(encryptedMsg);
		//Get result
		result = (String) inStream.readObject();
		return result;
	}
	
	/**
	 * Gets all unread messages of the current user by sending the read command
	 * to the server
	 * 
	 * @return								All unread messages from the current user
	 * @throws ClassNotFoundException		When trying to find the class of an object
	 * 										that does not match/exist
	 * @throws IOException					When inStream does not receive input
	 * 										or the outStream can't send a message	
	 * @throws NoSuchAlgorithmException 	If the requested algorithm is not available
	 * @throws KeyStoreException 			If an exception occurs while accessing the keystore
	 * @throws UnrecoverableKeyException 	If the key cannot be recovered
	 * @throws BadPaddingException 			If the padding is invalid
	 * @throws IllegalBlockSizeException 	If the block size is invalid
	 * @throws NoSuchPaddingException 		If the padding scheme is not available
	 * @throws InvalidKeyException 			If the key is invalid
	 */
	@SuppressWarnings("unchecked")
	public String read()
			throws ClassNotFoundException, IOException, UnrecoverableKeyException,
			KeyStoreException, NoSuchAlgorithmException, InvalidKeyException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		//Send read command
		outStream.writeObject("read");
		//Get result
		Stack<Message> msgList = (Stack<Message>) inStream.readObject();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("You have " + msgList.size() + " messages to read! " + EOL);
		
		while(!msgList.isEmpty()) {
			
			Message m = msgList.pop();
			
			AssimetricMessageDecryption amd = new AssimetricMessageDecryption(this.keystore, this.keystorePwd);
			
			sb.append("From: " + m.getFrom() + ": " + amd.decrypt(m.getContent()) + EOL);
		}
		return sb.toString();
	}

	/**
	 * Gets all transactions of the current user by sending the list command
	 * to the server
	 * 
	 * @return							All transactions of the current user
	 * @throws IOException				When an I/O error occurs while reading/writing to a file
	 * @throws ClassNotFoundException	If the class of a serialized object is not found
	 */
	public String list() throws IOException, ClassNotFoundException {
		String result = "";
		//Send wallet command
		outStream.writeObject("list");
		//Get result
		result = (String) inStream.readObject();
		return result;
	}
}
