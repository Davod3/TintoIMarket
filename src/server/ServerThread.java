package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.NoSuchPaddingException;

import catalogs.UserCatalog;
import handlers.*;
import utils.FileIntegrityViolationException;

/**
 * This class represents the threads of the server of this application
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class ServerThread extends Thread {

	private Socket socket = null;
	private UserCatalog userCatalog;
	private String loggedUser = null;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;
	private KeyStore ks = null;
	
	/**
	 * Creates a new thread for the server to manage
	 * the commands from the client
	 * 
	 * @param inSoc				Client's socket
	 * @param ks 
	 * @throws IOException		When an I/O error occurs while 
	 * 							reading/writing to a file or stream
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public ServerThread(Socket inSoc, KeyStore ks) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		//Save client's socket
		this.socket = inSoc;
		//Get the unique instance of User Catalog
		this.userCatalog = UserCatalog.getInstance();
		//Open streams
		this.outStream = new ObjectOutputStream(socket.getOutputStream());
		this.inStream = new ObjectInputStream(socket.getInputStream());
		this.ks = ks;
		
		//Print message with result
		System.out.println("New connection established!");
	}

	/**
	 * Runs the engine of a server's thread
	 */
	public void run() {
		// Open IO streams
		try {
			
			if (authenticateUser()) {
				// User authenticated, wait for commands
				System.out.println("User authenticated");
				mainLoop();
			} else {
				// User failed to authenticate, close connection
				System.out.println("Authentication failed");
			}
		} catch (IOException e) {
			System.out.println("User exited");
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
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// Failed to retrieve certificate from file
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileIntegrityViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean authenticateUser() throws ClassNotFoundException, IOException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		
		String user = (String) inStream.readObject(); //Receive userID
		
		System.out.println("Received userID: " + user);
		
		long nonce = new Random().nextLong();
		
		System.out.println("Generated nonce: " + nonce);
		
		boolean isKnown = userCatalog.getUser(user) != null;
		
		System.out.println("Checked if user present" + isKnown);
		
		outStream.writeObject(nonce); //Send nonce
		System.out.println("Sent nonce");
		
		outStream.writeObject(isKnown); //isKnown flag
		System.out.println("Sent flag");
		
		if(isKnown) {
			//Authenticate
			SignedObject signedNonce = (SignedObject) inStream.readObject();
			Certificate cert = this.userCatalog.getUserCertificate(user);
			
			long receivedNonce = (Long) signedNonce.getObject();
			
			if(receivedNonce == nonce) {
				
				PublicKey received = cert.getPublicKey();
				
				if(signedNonce.verify(received, Signature.getInstance("MD5withRSA"))) {
					
					outStream.writeObject(true);
					this.loggedUser = user;
					return true;
				}
			}
			
			
			
		} else {
			//Register
			SignedObject signedNonce = (SignedObject) inStream.readObject();
			System.out.println("Received signed nonce");
			
			
			Certificate cert = (Certificate) inStream.readObject();
			System.out.println("Received certificate");
			
			long receivedNonce = (Long) signedNonce.getObject();
			
			if(receivedNonce == nonce) {
				//Same as sent
				
				PublicKey received = cert.getPublicKey();
				
				if(signedNonce.verify(received, Signature.getInstance("MD5withRSA"))) {
					
					this.loggedUser = this.userCatalog.registerUser(user, cert);
					outStream.writeObject(true);
					return true;
					
				}
				
				
			} 
			
		}
		
		outStream.writeObject(false);
		return false;
	}

	/**
	 * Processes the commands from client
	 * 
	 * @throws ClassNotFoundException		When trying to find the class of an object
	 * 										that does not match/exist
	 * @throws IOException					When an I/O error occurs while
	 * 										reading/writing to a file or stream
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws FileIntegrityViolationException 
	 * @throws KeyStoreException 
	 * @throws CertificateException 
	 */
	private void mainLoop() throws ClassNotFoundException, IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, FileIntegrityViolationException, KeyStoreException, CertificateException {
		// Run main command execution logic
		while (this.socket.isConnected()) {
			//Get command
			System.out.println("Waiting for commands...");
			String command = (String) inStream.readObject();
			System.out.println("Command: " + command);
			//Run the command
			switch (command) {
			//Each command has a unique handler
			case "add":
				AddHandler.getInstance().run(inStream, outStream);
				break;
			case "sell":
				SellHandler.getInstance().run(inStream, outStream, loggedUser);
				break;

			case "view":
				ViewHandler.getInstance().run(inStream, outStream);
				break;

			case "buy":
				BuyHandler.getInstance().run(inStream, outStream, loggedUser);
				break;

			case "wallet":
				WalletHandler.getInstance().run(outStream, loggedUser);
				break;
			case "classify":
				ClassifyHandler.getInstance().run(inStream, outStream);
				break;

			case "talk":
				TalkHandler.getInstance().run(inStream, outStream, loggedUser);
				break;

			case "read":
				ReadHandler.getInstance().run(outStream, loggedUser);
				break;
				
			case "getCertificate":
				GetCertificateHandler.getInstance().run(inStream, outStream);

			default:
				//Default case for wrong command message
				break;
			}
		}
	}
}
