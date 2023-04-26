package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

/**
 * The VerifyHash class has some functions and strings that are often used in
 * this project (for hash verification).
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut 		nº 56339
 */
public class VerifyHash {
	
	private static VerifyHash instance = null;
	private Map<String, byte[]> file_hash;
	private static final String HASH_STORAGE = "server_files/storage/hash/";
	private static final String HASH_FILES = "server_files/storage/hash/hashfiles.txt";
	private static final String EOL = System.lineSeparator();
	private static final String SECRET_KEY_ALIAS = "mackey";
	private SecretKey secret = null;

	/**
	 * Initializes the VerifyHash class
	 * 
	 * @throws IOException				When is not possible to read/write to a file
	 * @throws ClassNotFoundException	When trying to find the class of an object
	 * 									that does not match/exist
	 */
	private VerifyHash() throws IOException, ClassNotFoundException {
		file_hash = loadHash();
	}
	
	/**
	 * Loads a map of file paths and their corresponding byte array
	 * hashes from the hash files directory
	 * 
	 * @return							A map containing file paths
	 * 									as keys and their corresponding
	 * 									byte array hashes as values
	 * @throws IOException				When is not possible to read/write to a file
	 * @throws ClassNotFoundException	When trying to find the class of an object
	 * 									that does not match/exist
	 */
	private Map<String, byte[]> loadHash()
			throws IOException, ClassNotFoundException {
		
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
			
			ois.close();
			fis.close();
		}
		
		br.close();
		
		return map;
	}

	/**
	 * Returns the unique instance of this class
	 * 
	 * @return							The unique instance of this class
	 * @throws IOException				When an error occurs while reading from a file
	 * @throws ClassNotFoundException	When trying to find the class of an object
	 * 									that does not match/exist
	 */
	public static VerifyHash getInstance()
			throws IOException, ClassNotFoundException {
		if (instance == null)
			instance = new VerifyHash();
		return instance;
	}
	
	/**
	 * Verifies the integrity of a given file
	 * 
	 * @param file									The file to verify
	 * @param fileName								The name of the file to verify
	 * @throws IOException							If there was an error reading
	 * 												from the input file stream
	 * @throws ClassNotFoundException				When trying to find the class of an object
	 * 												that does not match/exist
	 * @throws NoSuchAlgorithmException				If the requested algorithm is not available
	 * @throws FileIntegrityViolationException		If the loaded file is corrupted
	 * @throws InvalidKeyException					If the key is invalid
	 */
	public void verify(File file, String fileName)
			throws IOException, ClassNotFoundException, NoSuchAlgorithmException,
			FileIntegrityViolationException, InvalidKeyException {
		
		int fileLen = (int) file.length();
		
		if(fileLen > 0) {
			
			byte[] fileBytes = new byte[fileLen];
			
			FileInputStream fis = new FileInputStream(file);
			fis.read(fileBytes);
			fis.close();
			
			this.verify(fileBytes, fileName);
		}
	}
	
	/**
	 * Verifies if the hash of the file matches it's stored hash
	 * 
	 * @param file									The file to verify
	 * @param fileName								The name of the file to verify													
	 * @throws NoSuchAlgorithmException				If the requested algorithm is not available
	 * @throws FileIntegrityViolationException		If the loaded file is corrupted
	 * @throws InvalidKeyException					If the key is invalid 					
	 */
	public void verify(byte[] file, String fileName)
			throws NoSuchAlgorithmException, FileIntegrityViolationException,
			InvalidKeyException {
			
			byte[] storedHash = this.file_hash.get(fileName);

			if(storedHash != null) {
				
				Mac mac = Mac.getInstance("HmacSHA1");
				mac.init(this.secret);
				mac.update(file);
				byte[] newHash = mac.doFinal();
				
				if(!isEqual(storedHash, newHash)) {
					throw new FileIntegrityViolationException("File " + fileName + " integrity was violated!");
				}
			} else {
				throw new FileIntegrityViolationException("File " + fileName + " integrity cannot be assessed!");
			}
	}
	
	/**
	 * Updates the hash of the given file
	 * 
	 * @param file									The file to update the hash (byte [])
	 * @param fileName								The name of the file to update the hash
	 * @throws NoSuchAlgorithmException				If the requested algorithm is not available
	 * @throws IOException							If there was an error reading
	 * 												from the input file stream
	 * @throws ClassNotFoundException				When trying to find the class of an object
	 * 												that does not match/exist				
	 * @throws FileIntegrityViolationException		If the loaded file is corrupted
	 * @throws InvalidKeyException					If the key is invalid					
	 */
	public void updateHash(byte[] file, String fileName)
			throws NoSuchAlgorithmException, IOException,
			ClassNotFoundException, FileIntegrityViolationException,
			InvalidKeyException {
		
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(this.secret);
		mac.update(file);
		byte[] hash =  mac.doFinal();
		
		if(!this.file_hash.containsKey(fileName)) {
			
			//If file not hashed, do so
			
			File hashes = new File(HASH_FILES);
			hashes.getParentFile().mkdirs();
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(HASH_FILES, true));
			bw.append(fileName + EOL);
			bw.close();
			
		} else {
			
			//If file already hashed, check integrity
			File oldFile = new File(fileName);
			this.verify(oldFile, fileName);
			
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

	/**
	 * Sets the privateKey to the given value
	 * 
	 * @param ks							The KeyStore
	 * @param keystorePwd					The KeyStore password
	 * @throws UnrecoverableKeyException	If the key cannot be recovered
	 * @throws KeyStoreException			If an exception occurs while accessing the keystore
	 * @throws NoSuchAlgorithmException		If the requested algorithm is not available
	 */
	public void setPrivateKey(KeyStore ks, String keystorePwd)
			throws UnrecoverableKeyException, KeyStoreException,
			NoSuchAlgorithmException {
		this.secret = (SecretKey) ks.getKey(SECRET_KEY_ALIAS, keystorePwd.toCharArray());
	}
	
	/**
	 * Checks if two hashes are the same
	 * 
	 * @param newHash		The first hash recently calculated
	 * @param oldHash		The hash from the file
	 * @return				True if the given hashes are equal, false otherwise
	 */
	private boolean isEqual(byte[] newHash, byte[] oldHash) {
		if(newHash.length != oldHash.length) {
			return false;
		}
		
		for(int i = 0; i < newHash.length; i++) {
			if(newHash[i] != oldHash[i]) {
				return false;
			}
		}
		
		return true;
	}
}
