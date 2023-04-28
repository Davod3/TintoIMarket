package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import utils.PBE;
import utils.VerifyHash;
import utils.FileIntegrityViolationException;
import utils.LogUtils;

/**
 * This class represents the server of this application
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class Server {
	
	private int sPort;
	private boolean close = false;
	private static final String SERVER_FILES_DIR = "server_files/";
	private KeyStore ks = null;
	private String keystorePath;
	private static final String ALIAS_KEY = "server";
	private String keystorePwd;
	
	/**
	 * Creates a new Server given the port, a password to encrypt/decrypt the users file,
	 * the keystore and it's password
	 * 
	 * @param port								The port for the connection to the server
	 * @param keystorepw 						The password of the Keystore
	 * @param keystore 							The KeyStore
	 * @param cipherpw 							The password to use with cipher encryption/decryption
	 * @throws KeyStoreException 				If an exception occurs while accessing the keystore
	 * @throws IOException 						When an I/O error occurs while reading/writing to a file
	 * @throws CertificateException 			When an error occurs while generating the certificate from the fileInputStream
	 * @throws NoSuchAlgorithmException 		If the requested algorithm is not available
	 * @throws ClassNotFoundException 			When trying to find the class of an object
	 * 											that does not match/exist
	 * @throws UnrecoverableKeyException 		If the key cannot be recovered
	 * @throws FileIntegrityViolationException 	If the loaded file's is corrupted
	 * @throws SignatureException 				When an error occurs while signing an object
	 * @throws InvalidKeyException 				If the key is invalid
	 */
	public Server(int port, String cipherpw, String keystore, String keystorepw)
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, ClassNotFoundException,
			UnrecoverableKeyException, FileIntegrityViolationException,
			InvalidKeyException, SignatureException {
		this.sPort = port;
		this.ks = getKeyStore(keystore, keystorepw.toCharArray());
		this.keystorePath = keystore;
		this.keystorePwd = keystorepw;
		
		//Criar PBE singleton com a password cipherpw;
		PBE.getInstance().setPBE(cipherpw);
				
		//Set private key for hashing
		VerifyHash.getInstance().setPrivateKey(this.ks, this.keystorePwd);
		
		LogUtils.getInstance().setKeyStore(ks, ALIAS_KEY, keystorepw);
		
		if(!LogUtils.getInstance().verifyBlockchainIntegrity())
			throw new FileIntegrityViolationException("Blockchain integrity was violated!");
	}

	/**
	 * Gets the KeyStore from the given file path, using the given password
	 * 
	 * @param keystore						The file path of the KeyStore
	 * @param keystorepw					The password of the KeyStore
	 * @return								The KeyStore
	 * @throws KeyStoreException			If an exception occurs while accessing the keystore
	 * @throws NoSuchAlgorithmException		If the requested algorithm is not available
	 * @throws CertificateException			When an error occurs while generating the certificate
	 * 										from the fileInputStream
	 * @throws IOException					When inStream does not receive input
	 * 										or the outStream can't send the result message
	 */
	private KeyStore getKeyStore(String keystore, char[] keystorepw)
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException {
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		File ks_file = new File(keystore);
		FileInputStream fis = new FileInputStream(ks_file);
		ks.load(fis, keystorepw);
		
		return ks;
	}

	/**
	 * Runs the server's engine
	 * 
	 * @throws InvalidAlgorithmParameterException 	If an invalid algorithm parameter is passed to a method
	 * @throws NoSuchPaddingException 				If the padding scheme is not available
	 * @throws InvalidKeySpecException 				If the requested key specification is invalid
	 * @throws NoSuchAlgorithmException 			If the requested algorithm is not available
	 * @throws InvalidKeyException 					If the key is invalid
	 * @throws FileIntegrityViolationException 
	 */
	public void run()
			throws InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, FileIntegrityViolationException {
		//Create socket for server
		System.setProperty("javax.net.ssl.keyStore", this.keystorePath);
		System.setProperty("javax.net.ssl.keyStorePassword", this.keystorePwd);
		ServerSocketFactory ssf = SSLServerSocketFactory.getDefault();
		ServerSocket sSoc = null;
		//Open socket on the defined port
		try {
			sSoc = (SSLServerSocket) ssf.createServerSocket(sPort);
			System.out.println("Listening on port " + sPort);
		} catch (IOException e) {
			System.out.println("Failed to open new server socket!");
			System.out.println(e.getMessage());
		}
		
		File serverDir = new File(SERVER_FILES_DIR);
		serverDir.mkdir();
		
		//Wait for connections
		while(!this.close) {
			try {
				//Try to catch new connections to clients
				Socket inSoc = sSoc.accept();
				//Create a new thread for the client
				ServerThread workerThread = new ServerThread(inSoc, ks);
				workerThread.start();
			} catch (IOException e) {
				System.out.println("Failed to connect to client!");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Stops the server main loop
	 */
	public void stop() {
		this.close = true;
	}
}
