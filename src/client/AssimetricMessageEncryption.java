package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AssimetricMessageEncryption {

	private KeyStore truststore;
	private String pwd;
	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;

	public AssimetricMessageEncryption(KeyStore truststore, String truststorepwd, ObjectInputStream inStream,
			ObjectOutputStream outStream) {

		this.truststore = truststore;
		this.pwd = truststorepwd;
		this.inStream = inStream;
		this.outStream = outStream;

	}

	public byte[] encrypt(String receiver, String message) throws KeyStoreException, IOException, ClassNotFoundException,
			NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		// Get public key certificate
		Certificate cert = this.truststore.getCertificate(receiver);

		// Check if certificate already in keystore
		if (cert == null) {

			// If not, get certificate from server
			outStream.writeObject("getCertificate");
			cert = (Certificate) inStream.readObject();

		}

		// Get public key from certificate
		PublicKey key = cert.getPublicKey();

		// Encrypt using public key
		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.ENCRYPT_MODE, key);
		
		return c.doFinal(message.getBytes());

	}
	
	public String decrypt() {
		return null;
	}

}
