package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.AlgorithmParameters;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PBE {
	
	private final byte[] SALT = { (byte) 0xc9, (byte) 0x36, (byte) 0x78, (byte) 0x99, (byte) 0x52, (byte) 0x3e, (byte) 0xea, (byte) 0xf2 };
	
	public SecretKey generatePBEKey (String password) throws Exception {
		// Generate the key based on the password
		PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), SALT, 20); // pass, salt, iterations
		SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
		SecretKey key = kf.generateSecret(keySpec);
		return key;
	}
	
	public static String getFileName(String filename) {
		File file = new File(filename);
		String nameWithoutExt = file.getName().replaceFirst("[.][^.]+$", "");
		return nameWithoutExt;
	}
	
	public byte[] encryption (String filename, String password) throws Exception {
		SecretKey key = generatePBEKey(password);
		
		// ENCRYPTION: Lets check that the two keys are equivalent by encrypting a string
		Cipher c = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
		c.init(Cipher.ENCRYPT_MODE, key);
		
		FileInputStream fis = new FileInputStream(filename);
		FileOutputStream fos = new FileOutputStream("users.cif");
		
		CipherOutputStream cos = new CipherOutputStream(fos, c);
	    byte[] b = new byte[16];  
	    int i = fis.read(b);
	    while (i != -1) {
	        cos.write(b, 0, i);
	        i = fis.read(b);
	    }
	    
	    cos.close();
	    fis.close();
	    fos.close();
	    
		byte[] params = c.getParameters().getEncoded(); // we need to get the various parameters (p.ex., IV)
		return params;
	}
	
	public void decryption (String filename, String password, byte[] params) throws Exception {
		SecretKey key = generatePBEKey(password);
		
		// DECRYPTION: Now lets see if we get the original string (NOTE: get key exactly as above)
		AlgorithmParameters p = AlgorithmParameters.getInstance("PBEWithHmacSHA256AndAES_128");
		p.init(params);
		
		Cipher d = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
		d.init(Cipher.DECRYPT_MODE, key, p);
		
		FileInputStream fis = new FileInputStream(filename);
		CipherInputStream cis = new CipherInputStream(fis, d);
		FileOutputStream fos = new FileOutputStream("b.txt");
		
		byte[] b = new byte[16];  
	    int i = cis.read(b);
	    while (i != -1) {
	        fos.write(b, 0, i);
	        i = cis.read(b);
	    }
		
	    cis.close();
	    fis.close();
		fos.close();
	}
}
