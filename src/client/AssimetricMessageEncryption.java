package client;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AssimetricMessageEncryption {

	private KeyStore truststore;
	private String pwd;
	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;
	private String truststorePath;

	public AssimetricMessageEncryption(KeyStore truststore, String truststorepwd, ObjectInputStream inStream,
			ObjectOutputStream outStream, String truststorePath) {

		this.truststore = truststore;
		this.pwd = truststorepwd;
		this.inStream = inStream;
		this.outStream = outStream;
		this.truststorePath = truststorePath;

	}

	public byte[] encrypt(String receiver, String message) throws KeyStoreException, IOException, ClassNotFoundException,
			NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, CertificateException {

		// Get public key certificate
		Certificate cert = this.truststore.getCertificate(receiver);

		// Check if certificate already in keystore
		if (cert == null) {
			
			// If not, get certificate from server
			outStream.writeObject("getCertificate");
			outStream.writeObject(receiver);
			cert = (Certificate) inStream.readObject();	
			
			//Save it to truststore
			this.truststore.setCertificateEntry(receiver, cert);
			FileOutputStream out = new FileOutputStream(this.truststorePath);
			this.truststore.store(out, this.pwd.toCharArray());
			
			System.out.println("This happens");
		}
		
		// Get public key from certificate
		PublicKey key = cert.getPublicKey();
		
		// Encrypt using public key
		Cipher c = Cipher.getInstance("RSA");
		
		c.init(Cipher.ENCRYPT_MODE, key);
		
		return c.doFinal(message.getBytes());

	}
	
	public String decrypt() {
		return null;
	}

}
