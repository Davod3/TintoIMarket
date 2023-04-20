package utils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import catalogs.WineCatalog;

public class VerifyHash {
	
	private static VerifyHash instance = null;
	private Map<String, byte[]> file_hash;
	
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
	 */
	public boolean verify(byte[] file, String fileName) {
		
		
		
		return false;
	}
	
	public void updateHash(byte[] file, String fileName) throws NoSuchAlgorithmException {
		
		MessageDigest md = MessageDigest.getInstance("SHA");
		this.file_hash.put(fileName, md.digest(file)); 
		
		for(String f : file_hash.keySet()) {
			
			byte[] hash = file_hash.get(f);
			System.out.println(hash.toString());
		}
		
		
	}

}
