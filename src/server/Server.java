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
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import utils.PBE;
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
	 * Creates a new Server given the port
	 * 
	 * @param port		The port for the connection to the server
	 * @param keystorepw 
	 * @param keystore 
	 * @param cipherpw 
	 * @throws Exception 
	 */
	public Server(int port, String cipherpw, String keystore, String keystorepw) throws Exception {
		this.sPort = port;
		this.ks = getKeyStore(keystore, keystorepw.toCharArray());
		this.keystorePath = keystore;
		this.keystorePwd = keystorepw;
		//Criar PBE singleton com a password cipherpw;
		PBE.getInstance().setPBE(cipherpw);
		
		LogUtils.getInstance().setKeyStore(ks, ALIAS_KEY, keystorepw);
		if(!LogUtils.getInstance().verifyBlockchainIntegrity())
			throw new Exception("The blockchain was corrupted");
	}

	private KeyStore getKeyStore(String keystore, char[] keystorepw) throws KeyStoreException,
	NoSuchAlgorithmException, CertificateException, IOException {
		
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		File ks_file = new File(keystore);
		FileInputStream fis = new FileInputStream(ks_file);
		ks.load(fis, keystorepw);
		
		return ks;
	}

	/**
	 * Runs the server's engine
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public void run() throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException {
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
				System.out.println(e.getMessage());
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
