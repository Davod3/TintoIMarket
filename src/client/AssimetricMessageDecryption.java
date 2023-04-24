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
import javax.crypto.SecretKey;

public class AssimetricMessageDecryption {
	
	private KeyStore ks;
	private String keystorePwd;
	private static final String KEYPAIR_ALIAS = "client";
	
	public AssimetricMessageDecryption(KeyStore ks, String keystorePwd) {
		
		this.ks = ks;
		this.keystorePwd = keystorePwd;
		
	}	
	
	public String decrypt(byte[] encryptedMessage) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
			
			PrivateKey sk = (PrivateKey) ks.getKey(KEYPAIR_ALIAS, keystorePwd.toCharArray());
			
			Cipher c = Cipher.getInstance("RSA");
			c.init(Cipher.DECRYPT_MODE, sk);
			
			byte[] messageBytes = c.doFinal(encryptedMessage);
			
			return new String(messageBytes, StandardCharsets.UTF_8);
			
		}
}
