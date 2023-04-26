package utils;

import java.io.ByteArrayOutputStream;
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

/**
 * The PBE class has some functions and strings that are often used in
 * this project (Password Based Encryption management).
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut 		nº 56339
 */
public class PBE {
	
	private static PBE instance = null;
	
	private static final String PARAMS_FILE_PATH = "server_files/storage/params.txt";
	private static final String USER_FILE_PATH = "server_files/storage/users.cif";
	
	private static final byte[] SALT = { (byte) 0xc9, (byte) 0x36, (byte) 0x78, (byte) 0x99, (byte) 0x52, (byte) 0x3e, (byte) 0xea, (byte) 0xf2 };
	
	private boolean paramsExists;
	
	private String pw = null;
	private byte[] params;
	
	/**
	 * Returns the unique instance of this class
	 * 
	 * @return	The unique instance of this class
	 */
	public static PBE getInstance() {
		if (instance == null) {
			instance = new PBE();
		}
		return instance;
	}
	
	/**
	 * This constructor initializes the instance of
	 * this class with the given password and reads
	 * the parameters for encryption/decryption from
	 * the params file
	 * 
	 * @param pw				The password to be used in encryption/decryption
	 * @throws IOException		When it is not possible to read the params file
	 */
	public PBE(String pw) throws IOException {
		this.pw = pw;
		readParamsFile();
	}
	
	/**
	 * Default constructor for PBE class
	 */
	protected PBE() {}
	
	/**
	 * Sets a new password for encryption/decryption
	 * 
	 * @param pw	The new password to set to
	 */
	public void setPBE(String pw) {
		this.pw = pw;
	}
	
	/**
	 * Writes the parameters of the encryption into the params file
	 * 
	 * @throws IOException	When it is not possible to write into the params file
	 */
	public void writeParamsFile() throws IOException {
		File param = new File(PARAMS_FILE_PATH);
		param.createNewFile();
		FileOutputStream fos = new FileOutputStream(param);
		fos.write(params);
		fos.close();
		paramsExists = true;
	}
	
	/**
	 * Reads the parameters for encryption/decryption from the params file
	 * 
	 * @throws IOException	When it is not possible to read from the params file
	 */
	public void readParamsFile() throws IOException {
		File param = new File(PARAMS_FILE_PATH);
		
		if (param.exists()) {
			paramsExists = true;
			this.params = new byte[(int) param.length()];
			FileInputStream fis = new FileInputStream(param);
			fis.read(params);
			fis.close();
		}
	}
	
	/**
	 * Generates the secret key to be used for encryption/decryption
	 * 
	 * @return								The secretKey to be used for
	 * 										encryption/decryption
	 * @throws NoSuchAlgorithmException		If the requested algorithm is not available
	 * @throws InvalidKeySpecException		If the requested key specification is invalid
	 */
	public SecretKey generatePBEKey() throws NoSuchAlgorithmException, InvalidKeySpecException  {
		// Generate the key based on the password
		PBEKeySpec keySpec = new PBEKeySpec(pw.toCharArray(), SALT, 20); // pass, salt, iterations
		SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
		return kf.generateSecret(keySpec);
	}
	
	/**
	 * Returns the name of the file without the extension
	 * 
	 * @param filename		The filename with its extension
	 * @return				The filename without its extension
	 */
	public String getFileName(String filename) {
		File file = new File(filename);
		return file.getName().replaceFirst("[.][^.]+$", "");
	}
	
	/**
	 * Encrypts the given data to the users file
	 * 
	 * @param data							The data to encrypt
	 * @throws NoSuchAlgorithmException		If the requested algorithm is not available
	 * @throws NoSuchPaddingException		If the padding scheme is not available
	 * @throws InvalidKeyException			If the key is invalid
	 * @throws InvalidKeySpecException		If the requested key specification is invalid
	 * @throws IOException					When inStream does not receive input
	 * 										or the outStream can't send the result message
	 */
	public void encryption (String data)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidKeySpecException, IOException {
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
	
	/**
	 * Decrypts the given file and returns the decrypted content
	 * 
	 * @param file									The file to decrypt
	 * @return										A string with the content decrypted
	 * @throws NoSuchAlgorithmException				If the requested algorithm is not available
	 * @throws InvalidKeySpecException				If the requested key specification is invalid
	 * @throws IOException							When inStream does not receive input
	 * 												or the outStream can't send the result message
	 * @throws NoSuchPaddingException				If the padding scheme is not available
	 * @throws InvalidKeyException					If the key is invalid
	 * @throws InvalidAlgorithmParameterException	If an invalid algorithm parameter is passed to a method
	 */
	public String decryption (File file)
			throws NoSuchAlgorithmException, InvalidKeySpecException,
			IOException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException {
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
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int len;
			byte[] buffer = new byte[1024];
			while((len = cis.read(buffer, 0, buffer.length)) != -1) {
				baos.write(buffer, 0 , len);
			}
			
			baos.flush();
			byte[] decryptedBytes = baos.toByteArray();
			sb.append(new String(decryptedBytes,StandardCharsets.UTF_8));
			
		    cis.close();
		    fis.close();
		    return sb.toString();
		}
		else {
			return null;
		}
	}
}
