package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

import utils.FileIntegrityViolationException;

/**
 * This class represents the threads of the server of this application
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class TintolMarketServer {

	/**
	 * Starts the server with the given IP address and Port number
	 * 
	 * @param args									The address of the server
	 * @throws InvalidAlgorithmParameterException 	If an invalid algorithm parameter is passed to a method
	 * @throws NoSuchPaddingException 				If the padding scheme is not available
	 * @throws InvalidKeySpecException 				If the requested key specification is invalid
	 * @throws InvalidKeyException 					If the key is invalid
	 */
	public static void main(String[] args)
			throws InvalidKeyException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidAlgorithmParameterException {
		//Default port for connection
		int port = 12345;
		
		String cipherpw = null;
		String keystore = null;
		String keystorepw = null;
		
		//Check if port is present
		if(args.length >= 4){
			
			//Set port
			try {
				port = Integer.parseInt(args[0]);
				cipherpw = args[1];
				keystore = args[2];
				keystorepw = args[3];
			} catch (Exception e) {
				System.out.println("Input argument must be a number. Using default port.");
			}
			
		} else if (args.length >= 3) {
			
			try {
				port = Integer.parseInt(args[0]);
				System.out.println("Missing argument. Correct usage TintolMarketServer <port> <cipher-password> <keystore> <keystore-password> ");
				return;
				
			} catch (Exception e) {
				cipherpw = args[0];
				keystore = args[1];
				keystorepw = args[2];
			}
		}
		
		//Create Server
		Server myServer;
		try {
			
			System.out.println(keystorepw);
			myServer = new Server(port, cipherpw, keystore, keystorepw);
			myServer.run();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			System.out.println("Error acessing key store!");
		} catch (FileNotFoundException e) {
			
			System.out.println("Key Store not found!");
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileIntegrityViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
