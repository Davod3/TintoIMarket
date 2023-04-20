package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import catalogs.WineCatalog;

public class VerifyHash {
	
	private static VerifyHash instance = null;
	private Map<String, byte[]> file_hash;
	private static final String HASH_STORAGE = "server_files/storage/hash/";
	
	private VerifyHash() {
		file_hash = new HashMap<>();
	}
	
	public static VerifyHash getInstance() {
		if (instance == null)
			instance = new VerifyHash();
		return instance;
	}
	
	/**
	 * Verify hash from file matches stored hash
	 * 
	 * @param file
	 * @param fileName
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws NoSuchAlgorithmException 
	 */
	public boolean verify(byte[] file, String fileName) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
		
		byte[] storedHash = this.file_hash.get(fileName);
		
		String[] splitFileName = fileName.split("/");	
		String filepath = HASH_STORAGE + splitFileName[splitFileName.length - 1] + "hash";

		if(storedHash != null) {
			
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] newHash = md.digest(file);
			
			return MessageDigest.isEqual(storedHash, newHash);	
		}
		
		
		return false;
	}
	
	public void updateHash(byte[] file, String fileName) throws NoSuchAlgorithmException, IOException {
		
		MessageDigest md = MessageDigest.getInstance("SHA");
		byte[] hash =  md.digest(file);
		this.file_hash.put(fileName, hash); 
		
		String[] splitFileName = fileName.split("/");	
		String filepath = HASH_STORAGE + splitFileName[splitFileName.length - 1] + "hash";
		
		File hashFile = new File(filepath);
		hashFile.getParentFile().mkdirs();
			
		FileOutputStream fos = new FileOutputStream(filepath);
		
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		
		oos.writeObject(hash);
		oos.close();
		fos.close();
	}

}
