package client;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * The AssimetricMessageDecryption class handles the asymmetric message decryption
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class AssimetricMessageDecryption {

	private KeyStore ks;
	private String keystorePwd;
	private static final String KEYPAIR_ALIAS = "client";

	/**
	 * This constructor takes a KeyStore and its password as parameters,
	 * which will be used to decrypt a message (using the private key)
	 * 
	 * @param ks			The KeyStore containing the private key needed
	 * @param keystorePwd	The password to unlock the KeyStore
	 */
	public AssimetricMessageDecryption(KeyStore ks, String keystorePwd) {
		this.ks = ks;
		this.keystorePwd = keystorePwd;
	}	

	/**
	 * Decrypts a message using the private key associated to the client
	 * 
	 * @param encryptedMessage				The encrypted message to be decrypted
	 * @return								The decrypted message
	 * @throws UnrecoverableKeyException	If the key cannot be recovered
	 * @throws KeyStoreException			If an exception occurs while accessing the keystore
	 * @throws NoSuchAlgorithmException		If the requested algorithm is not available
	 * @throws NoSuchPaddingException		If the padding scheme is not available
	 * @throws InvalidKeyException			If the key is invalid
	 * @throws IllegalBlockSizeException	If the block size is invalid
	 * @throws BadPaddingException			If the padding is invalid
	 */
	public String decrypt(byte[] encryptedMessage)
			throws UnrecoverableKeyException, KeyStoreException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		PrivateKey sk = (PrivateKey) ks.getKey(KEYPAIR_ALIAS, keystorePwd.toCharArray());

		Cipher c = Cipher.getInstance("RSA");
		c.init(Cipher.DECRYPT_MODE, sk);

		byte[] messageBytes = c.doFinal(encryptedMessage);

		return new String(messageBytes, StandardCharsets.UTF_8);
	}
}
