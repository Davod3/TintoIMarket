package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PBE {
	
	private static PBE instance = null;
	
	private static final String PARAMS_FILE_PATH = "server_files/storage/params.txt";
	private static final String USER_FILE_PATH = "server_files/storage/users.cif";
	
	private static final byte[] SALT = { (byte) 0xc9, (byte) 0x36, (byte) 0x78, (byte) 0x99, (byte) 0x52, (byte) 0x3e, (byte) 0xea, (byte) 0xf2 };
	
	private boolean paramsExists;
	
	private String pw = null;
	private byte[] params;
	
	public static PBE getInstance() {
		if (instance == null) {
			instance = new PBE();
		}
		return instance;
	}
	public PBE(String pw) throws IOException {
		this.pw = pw;
		readParamsFile();
	}
	
	protected PBE() {
	}
	
	public void setPBE(String pw) {
		this.pw = pw;
	}
	
	public void writeParamsFile() throws IOException {
		File param = new File(PARAMS_FILE_PATH);
		param.createNewFile();
		FileOutputStream fos = new FileOutputStream(param);
		fos.write(params);
		fos.close();
		paramsExists = true;
	}
	
	public void readParamsFile() throws IOException {
		File param = new File(PARAMS_FILE_PATH);
		if (param.exists()) {
			paramsExists = true;
			FileInputStream fis = new FileInputStream(param);
			params = fis.readAllBytes();
			fis.close();
		}
	}
	
	public SecretKey generatePBEKey() throws NoSuchAlgorithmException, InvalidKeySpecException  {
		// Generate the key based on the password
		PBEKeySpec keySpec = new PBEKeySpec(pw.toCharArray(), SALT, 20); // pass, salt, iterations
		SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
		return kf.generateSecret(keySpec);
	}
	
	public String getFileName(String filename) {
		File file = new File(filename);
		return file.getName().replaceFirst("[.][^.]+$", "");
	}
	
	public void encryption (String data) throws NoSuchAlgorithmException,
	NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IOException {
		SecretKey key = generatePBEKey();
		
		Cipher c = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
		c.init(Cipher.ENCRYPT_MODE, key);
		
		byte[] dataBytes = data.getBytes();
		
		FileOutputStream fos = new FileOutputStream(USER_FILE_PATH);
		
		CipherOutputStream cos = new CipherOutputStream(fos, c);
	    
		cos.write(dataBytes);
		cos.flush();
		
	    cos.close();
	    fos.close();
	    
		this.params = c.getParameters().getEncoded(); // we need to get the various parameters (p.ex., IV)
		writeParamsFile();
	}
	
	public String decryption (File file) throws NoSuchAlgorithmException,
	InvalidKeySpecException, IOException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		readParamsFile();
		if (paramsExists) {
			SecretKey key = generatePBEKey();
			
			AlgorithmParameters p = AlgorithmParameters.getInstance("PBEWithHmacSHA256AndAES_128");
			p.init(params);
			
			Cipher d = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
			d.init(Cipher.DECRYPT_MODE, key, p);
			
			FileInputStream fis = new FileInputStream(file);
			CipherInputStream cis = new CipherInputStream(fis, d);
			
			StringBuilder sb = new StringBuilder();
			
			byte[] b = cis.readAllBytes();
			sb.append(new String(b,StandardCharsets.UTF_8));
			
			//byte[] b = new byte[16];  
		    //int i = cis.read(b);
		    /*while (i != -1) {
		        sb.append(new String(b,StandardCharsets.UTF_8));
		        i = cis.read(b);
		    }*/
			
		    cis.close();
		    fis.close();
		    return sb.toString();
		}
		else {
			return null;
		}
	}
}
