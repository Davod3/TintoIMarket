package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * This class represents the session handler for the client of this application.
 * It digests the user commands for the NetworkClient class. Also sends error messages
 * when receiving exceptions resulting of entered commands.
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class SessionHandler {
	
	private boolean sessionValid;
	private NetworkClient netClient;
	public static final String COMMAND_ERROR = "Invalid command.";
		
	/**
	 * Creates a new SessionHandler given the user, password
	 * and the address of the server to connect to.
	 * 
	 * @param user						The user who's trying to log in
	 * @param password					The user's password
	 * @param serverAdress				The address of the server to connect to
	 * @throws IOException				When an I/O error occurs while reading/writing to a file
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyStoreException 
	 */
	public SessionHandler(String serverAdress, String truststore, String keystore, String keystorePassword ,String user)
			throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
		this.netClient = new NetworkClient(serverAdress, truststore, keystore, keystorePassword);
		sessionValid = netClient.validateSession(getKeyStore(keystore, keystorePassword.toCharArray()), keystorePassword, user);
	}
	
	/**
	 * Checks if the session is still valid.
	 * 
	 * @return	True if the session is still valid, false otherwise
	 */
	public boolean getSessionValid() {
		return sessionValid;
	}
	
	/**
	 * Sends the respective commands to NetworkClient class.
	 * This function sanitizes the commands as well and receives
	 * the answer from the server.
	 * 
	 * @param command		The command entered by the current user
	 * @return				Result message received by NetworkClient
	 * @throws NoSuchAlgorithmException 
	 * @throws SignatureException 
	 * @throws InvalidKeyException 
	 */
	public String processCommand(String[] command) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
		String result = "";
		if (command[0].equals("talk") || command[0].equals("t")) {
			StringBuilder builder = new StringBuilder();
			
			for (int i = 2; i < command.length; i++) {
				builder.append(command[i] + " ");
			}
			
			try {
				result = netClient.talk( command[1], builder.toString());
			} catch (ClassNotFoundException | IOException | ArrayIndexOutOfBoundsException e) {
				System.out.println("Error sending message. Invalid user.\n");
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			switch (command.length) {
			case 1:
				if (command[0].equals("read") || command[0].equals("r")) {
					try {
						result = netClient.read();
					} catch (ClassNotFoundException | IOException e) {
						System.out.println("Error reading messages\n");
						System.exit(-1);
					} catch (UnrecoverableKeyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (KeyStoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalBlockSizeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BadPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (command[0].equals("wallet") || command[0].equals("w")) {
					try {
						result = netClient.wallet();
					} catch (ClassNotFoundException | IOException e) {
						System.out.println("Error getting balance\n");
						System.exit(-1);
					}
				}
				else {
					System.out.println(COMMAND_ERROR);
				}
				break;
			case 2:
				if (command[0].equals("view") || command[0].equals("v") ) {
					try {
						result = netClient.view(command[1]);
					} catch (ClassNotFoundException | IOException e) {
						System.out.println("Error viewing wine\n");
						System.exit(-1);
					}
				}
				else {
					System.out.println(COMMAND_ERROR);
				}
				break;
			case 3:
				if (command[0].equals("add") || command[0].equals("a")) {
					try {
						result = netClient.add(command[1], command[2]);
					} catch (ClassNotFoundException | IOException e) {
						System.out.println("Error adding wine\n");
						System.exit(-1);
					}
				}
				else if (command[0].equals("classify") || command[0].equals("c")) {
					try {
						result = netClient.classify(command[1], command[2]);
					} catch (ClassNotFoundException | IOException e) {
						System.out.println("Error classifying wine\n");
						System.exit(-1);
					}
				}
				else {
					System.out.println(COMMAND_ERROR);
				}
				break;
			case 4:
				if (command[0].equals("buy") || command[0].equals("b")) {
					try {
						result = netClient.buy(command[1], command[2], command[3]);
					} catch (ClassNotFoundException | IOException e) {
						System.out.println("Error buying wine\n");
						System.exit(-1);
					}
				}
				else if (command[0].equals("sell") || command[0].equals("s")) {
					try {
						result = netClient.sell(command[1], command[2], command[3]);
					} catch (ClassNotFoundException | IOException e) {
						System.out.println("Error selling wine\n");
						System.exit(-1);
					}
				}
				else {
					System.out.println(COMMAND_ERROR);
				}
				break;
			default:
				System.out.println(COMMAND_ERROR);	
				break;
			}	
		}
		
		return result;
	}
	private KeyStore getKeyStore(String keystore, char[] password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		File ks_file = new File(keystore);
		FileInputStream fis = new FileInputStream(ks_file);
		ks.load(fis, password);
		return ks;
	}
}
