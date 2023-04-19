package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

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
	private String keystorePwd;

	/**
	 * Creates a new Server given the port
	 * 
	 * @param port		The port for the connection to the server
	 * @param keystorepw 
	 * @param keystore 
	 * @param cipherpw 
	 * @throws KeyStoreException 
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 */
	public Server(int port, String cipherpw, String keystore, String keystorepw) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		this.sPort = port;
		this.ks = getKeyStore(keystore, keystorepw.toCharArray());
		this.keystorePath = keystore;
		this.keystorePwd = keystorepw;
		
		
	}

	private KeyStore getKeyStore(String keystore, char[] keystorepw) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, FileNotFoundException {
		
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		File ks_file = new File(keystore);
		FileInputStream fis = new FileInputStream(ks_file);
		ks.load(fis, keystorepw);
		
		return ks;
	}

	/**
	 * Runs the server's engine
	 */
	public void run() {
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
