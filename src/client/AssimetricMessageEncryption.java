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

/**
 * The AssimetricMessageEncryption class handles the asymmetric message encryption
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class AssimetricMessageEncryption {

	private KeyStore truststore;
	private String pwd;
	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;
	private String truststorePath;

	/**
	 * This constructor takes the Keystore and all the parameters to use for encrypting a message
	 * 
	 * @param truststore		The truststore containing the trusted certificates
	 * @param truststorepwd		The password for the truststore
	 * @param inStream			The input Stream used for communication
	 * @param outStream			The output Stream used for communication
	 * @param truststorePath	The file path for the truststore
	 */
	public AssimetricMessageEncryption(KeyStore truststore, String truststorepwd,
			ObjectInputStream inStream, ObjectOutputStream outStream, String truststorePath) {
		this.truststore = truststore;
		this.pwd = truststorepwd;
		this.inStream = inStream;
		this.outStream = outStream;
		this.truststorePath = truststorePath;
	}

	/**
	 * Encrypts a message using the public key of the receiver
	 * 
	 * @param receiver						The message receiver
	 * @param message						The message to send
	 * @return								The message encrypted
	 * @throws KeyStoreException			If an exception occurs while accessing the keystore
	 * @throws IOException					When an I/O error occurs while reading/writing to a file
	 * @throws ClassNotFoundException		If the class of a serialized object is not found
	 * @throws NoSuchAlgorithmException		If the requested algorithm is not available
	 * @throws NoSuchPaddingException		If the padding scheme is not available
	 * @throws InvalidKeyException			If the key is invalid
	 * @throws IllegalBlockSizeException	If the block size is invalid
	 * @throws BadPaddingException			If the padding is invalid
	 * @throws CertificateException			When an error occurs while generating the certificate
	 * 										from the fileInputStream
	 */
	public byte[] encrypt(String receiver, String message)
			throws KeyStoreException, IOException, ClassNotFoundException,
			NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, CertificateException {
		// Get public key certificate
		Certificate cert = this.truststore.getCertificate(receiver);

		// Check if certificate already in keystore
		if (cert == null) {

			// If not, get certificate from server
			outStream.writeObject("getCertificate");
			outStream.writeObject(receiver);
			cert = (Certificate) inStream.readObject();	

			if(cert != null) {
				//Save it to truststore
				this.truststore.setCertificateEntry(receiver, cert);
				FileOutputStream out = new FileOutputStream(this.truststorePath);
				this.truststore.store(out, this.pwd.toCharArray());
			} else {
				return new byte[0];
			}
		}

		// Get public key from certificate
		PublicKey key = cert.getPublicKey();

		// Encrypt using public key
		Cipher c = Cipher.getInstance("RSA");

		c.init(Cipher.ENCRYPT_MODE, key);

		return c.doFinal(message.getBytes());
	}
}
