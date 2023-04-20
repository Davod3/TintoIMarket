package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import catalogs.WineCatalog;
import domain.User;

public class VerifyHash {
	
	private static VerifyHash instance = null;
	private Map<String, byte[]> file_hash;
	private static final String HASH_STORAGE = "server_files/storage/hash/";
	private static final String HASH_FILES = "server_files/storage/hash/hashfiles.txt";
	private static final String EOL = System.lineSeparator();

	
	private VerifyHash() throws IOException, ClassNotFoundException {
		file_hash = loadHash();
	}
	
	private Map<String, byte[]> loadHash() throws IOException, ClassNotFoundException {
		
		HashMap<String, byte[]> map = new HashMap<>();
		
		File hashfile = new File(HASH_FILES);
		hashfile.getParentFile().mkdirs();
		hashfile.createNewFile();
		
		BufferedReader br = new BufferedReader(new FileReader(HASH_FILES));
		String line;
		
		while((line = br.readLine()) != null) {
			
			String[] splitFileName = line.split("/");	
			String filepath = HASH_STORAGE + splitFileName[splitFileName.length - 1] + "hash";
			
			File hashes = new File(filepath);
			hashes.getParentFile().mkdirs();
			
			FileInputStream fis = new FileInputStream(filepath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			byte[] hash = (byte[]) ois.readObject();
			
			
			String[] splitFilePath = filepath.split("hash");
			
			String filePathKey = splitFilePath[0] + splitFilePath[1].split("/")[1];
		
			map.put(filePathKey, hash);
		}
		
		return map;
	}

	public static VerifyHash getInstance() throws IOException, ClassNotFoundException {
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
	 * @throws FileIntegrityViolationException 
	 */
	public void verify(byte[] file, String fileName) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, FileIntegrityViolationException {
		
		byte[] storedHash = this.file_hash.get(fileName);
		
		String[] splitFileName = fileName.split("/");	
		String filepath = HASH_STORAGE + splitFileName[splitFileName.length - 1] + "hash";

		if(storedHash != null) {
			
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] newHash = md.digest(file);
			
			if(!MessageDigest.isEqual(storedHash, newHash)) {
				System.out.println("VerifyHash - 99");
				throw new FileIntegrityViolationException("File " + fileName + " integrity was violated!");
			}
		} else {
			throw new FileIntegrityViolationException("File " + fileName + " integrity cannot be assessed!");
		}
		
	}
	
	public void updateHash(byte[] file, String fileName) throws NoSuchAlgorithmException, IOException {
		
		MessageDigest md = MessageDigest.getInstance("SHA");
		byte[] hash =  md.digest(file);
		
		
		
		if(!this.file_hash.containsKey(fileName)) {
			
			File hashes = new File(HASH_FILES);
			hashes.getParentFile().mkdirs();
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(HASH_FILES, true));
			bw.append(fileName + EOL);
			bw.close();
			
		}
		
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
