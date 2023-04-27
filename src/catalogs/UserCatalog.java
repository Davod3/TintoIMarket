package catalogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.crypto.NoSuchPaddingException;

import domain.Message;
import domain.User;
import utils.FileUtils;
import utils.PBE;

/**
 * The UserCatalog class represents the catalog with all users
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class UserCatalog {

	private static UserCatalog instance = null;
	private Map<String, User> userList;
	
	private static final String USER_FILE_PATH = "server_files/storage/users.cif";
	private static final String USER_MESSAGES_PATH = "server_files/user_messages/";
	private static final String USER_MESSAGES_EXTENSION = ".txt";
	private static final String SEPARATOR = ":";
	private static final String CERTIFICATE_STORAGE = "server_files/storage/cert"; //cert is part of name, function adds unique uid
	private static final String CERTIFICATE_EXTENSION = ".cer";
	
	/**
	 * Creates a UserCatalog and loads all users from a users file
	 * 
	 * @throws IOException							When an I/O error occurs while loading all users
	 * @throws InvalidAlgorithmParameterException 	If an invalid algorithm parameter is invalid
	 * @throws NoSuchPaddingException 				If the padding scheme is not available
	 * @throws InvalidKeySpecException 				If the requested key specification is invalid
	 * @throws NoSuchAlgorithmException 			If the requested algorithm is not available
	 * @throws InvalidKeyException 					If the key is invalid for initializing the cipher
	 * @throws ClassNotFoundException 				If the class of a serialized object is not found
	 */
	private UserCatalog()
			throws IOException, InvalidKeyException,
			NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidAlgorithmParameterException,
			ClassNotFoundException {
		this.userList = loadUsers();
	}

	/**
	 * Returns a map with all users, after loading their unread messages
	 * 
	 * @return										A map containing all users 
	 * @throws IOException 							If an I/O error occurs when creating or opening the user file
	 * @throws InvalidAlgorithmParameterException 	If the algorithm parameters are invalid
	 * @throws NoSuchPaddingException 				If the padding scheme is not available
	 * @throws InvalidKeySpecException 				If the requested key specification is invalid
	 * @throws NoSuchAlgorithmException 			If the requested algorithm is not available
	 * @throws InvalidKeyException 					If the key is invalid
	 * @throws ClassNotFoundException 				If the class of a serialized object is not found
	 */
	private synchronized Map<String, User> loadUsers()
			throws IOException, InvalidKeyException,
			NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidAlgorithmParameterException,
			ClassNotFoundException {
		//Create a map to load users
		Map<String, User> users = new HashMap<>();
		//Open the file that contains all the users information
		File userFile = new File(USER_FILE_PATH);
		userFile.getParentFile().mkdirs();
		userFile.createNewFile(); //Make sure file exists before reading
		
		//Call decryption method
		String usersFile = PBE.getInstance().decryption(userFile);
		
		if (usersFile != null) {
			//Read each line of the file
			for (String line: usersFile.split(FileUtils.EOL)) {
				//Split the data
				String[] splitData = line.split(SEPARATOR);
				if (splitData.length >= 2) {
					//Create new user with the given name and password
					User user = new User(splitData[0]);
					//Load all unread messages of that user
					loadMessages(user);
					//Load user in map
					users.put(splitData[0], user);	
				}
			}
		}
		return users;
	}

	/**
	 * Loads the unread messages of the given user, which are in a specific file
	 * 
	 * @param user						The user for who we want to get the unread messages
	 * @throws IOException				When an I/O error occurs while reading from a file
	 * @throws ClassNotFoundException 	If the class of a serialized object is not found
	 */
	@SuppressWarnings("unchecked")
	private synchronized void loadMessages(User user)
			throws IOException, ClassNotFoundException {
		//Create the user path
		String filePath = USER_MESSAGES_PATH + user.getID() + USER_MESSAGES_EXTENSION;
		
		//Get the file with the specific user path
		File messageDir = new File(filePath);
		messageDir.getParentFile().mkdirs(); //Create parent directory if non existent
		messageDir.createNewFile(); //Create file before reading
		
		FileInputStream fileIn = new FileInputStream(filePath);
		
		if(fileIn.available() > 0) {
			
			ObjectInputStream in = new ObjectInputStream(fileIn);
			
			Stack<Message> inbox = (Stack<Message>) in.readObject();
			
			user.setInbox(inbox);
			
			in.close();
		}
		
		fileIn.close();	
		
	}

	/**
	 * Checks if a user has enough money
	 * 
	 * @param user		The user we want to see if has enough money
	 * @param value		The value to compare
	 * @return			True if user has balance greater than or equal to value,
	 * 					false otherwise
	 */
	public synchronized boolean hasEnoughMoney(String user, double value) {
		return getUser(user).getBalance() >= value;
	}
	
	/**
	 * Transfers a certain amount of money from the buyer to the seller
	 * 
	 * @param buyer		The user from which we want to transfer the value
	 * @param seller	The user to which we want to transfer the value
	 * @param value		The value we want to transfer
	 */
	public synchronized void transfer(String buyer, String seller, double value) {
		User buyerUser = getUser(buyer);
		User sellerUser = getUser(seller);
		buyerUser.setBalance(buyerUser.getBalance() - value);
		sellerUser.setBalance(sellerUser.getBalance() + value);
	}
	
	
	/**
	 * Returns the unique instance of the UserCatalog class.
	 * If there is no instance of the class, a new one is created and returned.
	 * 
	 * @return										The unique instance of the UserCatalog class
	 * @throws IOException							When an I/O error occurs while reading from a file
	 * @throws InvalidAlgorithmParameterException 	If the algorithm parameters are invalid
	 * @throws NoSuchPaddingException 				If the padding scheme is not available
	 * @throws InvalidKeySpecException 				If the requested key specification is invalid
	 * @throws NoSuchAlgorithmException 			If the requested algorithm is not available
	 * @throws InvalidKeyException 					If the key is invalid
	 * @throws ClassNotFoundException 				If the class of a serialized object is not found				
	 */
	public static UserCatalog getInstance()
			throws IOException, InvalidKeyException,
			NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidAlgorithmParameterException,
			ClassNotFoundException {
		if (instance == null)
			instance = new UserCatalog();
		return instance;
	}

	/**
	 * Registers a new user given the username and password
	 * 
	 * @param userId								Username
	 * @param cer									User's Certificate
	 * @return										Username
	 * @throws IOException							When an I/O error occurs while reading/writing to a file
	 * @throws CertificateEncodingException 		If there is an error encoding the user's certificate
	 * @throws InvalidAlgorithmParameterException 	If the algorithm parameters are invalid
	 * @throws NoSuchPaddingException 				If the padding scheme is not available
	 * @throws InvalidKeySpecException 				If the requested key specification is invalid
	 * @throws NoSuchAlgorithmException 			If the requested algorithm is not available
	 * @throws InvalidKeyException 					If the key is invalid
	 */
	public synchronized String registerUser(String userId, Certificate cert)
			throws IOException, CertificateEncodingException, InvalidKeyException,
			NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
			InvalidAlgorithmParameterException {
		
		//Save user certificate to file
		String certFileName = saveCertificate(userId, cert);
		
		//Create new user
		User registering = new User(userId);
		//Insert new user in dataBase
		userList.put(userId, registering);
		
		//Desencriptar ficheiro
		String usersFile = PBE.getInstance().decryption(new File(USER_FILE_PATH));
		
		//Adicionar user a String
		if (usersFile != null) {
			usersFile = usersFile + userId + SEPARATOR + certFileName + FileUtils.EOL;
		}
		else {
			usersFile = userId + SEPARATOR + certFileName + FileUtils.EOL;
		}
		//Encriptar string para o ficheiro
		PBE.getInstance().encryption(usersFile);	
		
		return userId;	
	}
	
	/**
	 * Saves a user's certificate
	 * 
	 * @param userId							The username of the user whose certificate we want to save
	 * @param cert								The certificate to be saved
	 * @return									The filename of the saved certificate
	 * @throws CertificateEncodingException		If there is an error encoding the user's certificate
	 * @throws IOException						When an I/O error occurs while reading/writing to a file
	 */
	private String saveCertificate(String userId, Certificate cert)
			throws CertificateEncodingException, IOException {
		
		String fileName = CERTIFICATE_STORAGE + userId + CERTIFICATE_EXTENSION;
		
		byte[] buf = cert.getEncoded();
		
		File certFile = new File(fileName);
		FileOutputStream fos = new FileOutputStream(certFile);
		
		fos.write(buf);
		fos.close();
		
		return fileName;
	}

	/**
	 * Gets all unread messages from the given user
	 * 
	 * @param loggedUser		The user for whom we want to get the messages
	 * @return					The unread messages
	 * @throws IOException		When an I/O error occurs while reading/writing to a file
	 */
	public synchronized Stack<Message> readMessages(String loggedUser)
			throws IOException {
		//Get user by username
		User user = getUser(loggedUser);
		//Get all unread messages
		Stack<Message> userInbox = user.getInbox();
		
		Stack<Message> result = new Stack<>();
		
		//Write all unread messages
		while(!userInbox.isEmpty()) {
			Message message = userInbox.pop();
			result.add(message);
		}
		updateMessages(user);
		return result;
	}
	
	/**
	 * Returns the user with the given username
	 * 
	 * @param user		The user to look for
	 * @return			The user with the given username
	 */
	public synchronized User getUser(String user) {
		return userList.get(user);
	}

	/**
	 * Checks if user with username exists in database
	 * 
	 * @param user		The user to check
	 * @return			True if user exists in database, false otherwise
	 */
	public synchronized boolean exists(String user) {
		return this.userList.containsKey(user);
	}

	/**
	 * Adds a new message to the mailBox of the given user
	 * 
	 * @param username			The user for whom we want to send a new message
	 * @param msgToSend			The message to send
	 * @throws IOException		When an I/O error occurs while reading/writing to a file
	 */
	public synchronized void addMessageToUser(String username, Message msgToSend)
			throws IOException {
		//Get user by username
		User user = userList.get(username);
		//Add message to user
		user.addMessage(msgToSend);
		updateMessages(user);	
	}

	/**
	 * Updates all unread messages to user file
	 * 
	 * @param user				The user for whom we want to update all unread messages
	 * @throws IOException		When an I/O error occurs while reading/writing to a file
	 */
	private synchronized void updateMessages(User user)
			throws IOException {
		//Get path to file with all unread messages
		String filePath = USER_MESSAGES_PATH + user.getID() + USER_MESSAGES_EXTENSION;
		//Get file with the specified path
		File messageDir = new File(filePath);
		messageDir.getParentFile().mkdirs(); //Create parent directory if non existent
		messageDir.createNewFile(); //Create file before reading
		//Open reader to read from file
		
		Stack<Message> messages = user.getInbox();
		
		FileOutputStream fileOut = new FileOutputStream(filePath);
		
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		
		out.writeObject(messages);
		
		out.close();
		fileOut.close();
	}

	/**
	 * Gets the certificate of the given user
	 * 
	 * @param user						The user for whom we want to get the certificate
	 * @return							The user's certificate
	 * @throws FileNotFoundException	When the certificate file for the user is not found
	 * @throws CertificateException		When an error occurs while generating the certificate from the fileInputStream
	 */
	public synchronized Certificate getUserCertificate(String user)
			throws FileNotFoundException, CertificateException {
		
		FileInputStream fis = new FileInputStream(CERTIFICATE_STORAGE + user + CERTIFICATE_EXTENSION);
		CertificateFactory cf = CertificateFactory.getInstance("X509");
		
		return cf.generateCertificate(fis);
	}
}
