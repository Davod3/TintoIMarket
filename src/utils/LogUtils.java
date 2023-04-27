package utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.NoSuchPaddingException;

import catalogs.UserCatalog;
import domain.Transaction;

/**
 * The LogUtils class has some functions and strings that are often used in this
 * project (transaction management).
 * 
 * @author André Dias nº 55314
 * @author David Pereira nº 56361
 * @author Miguel Cut nº 56339
 */
public class LogUtils {

	private static final String LOGS_FOLDER = "server_files/logs";
	private static LogUtils instance = null;
	private Block currentBlock;
	private KeyStore ks;
	private String alias;
	private String pwd;
	private static final String EOL = System.lineSeparator();

	/**
	 * This constructor loads the transactions from the log file
	 */
	private LogUtils() {
		File directory = new File(LOGS_FOLDER);
		if (!directory.exists())
			directory.mkdir();

		try {
			currentBlock = findLastBlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Adds a new transaction to the blockChain
	 * 
	 * @param transaction The transaction to be added
	 * @throws InvalidKeyException       If the key is invalid
	 * @throws UnrecoverableKeyException If the key cannot be recovered
	 * @throws SignatureException        When an error occurs while signing an
	 *                                   object
	 * @throws KeyStoreException         If an exception occurs while accessing the
	 *                                   keystore
	 * @throws NoSuchAlgorithmException  If the requested algorithm is not available
	 * @throws IOException               When inStream does not receive input or the
	 *                                   outStream can't send the result message
	 * @throws ClassNotFoundException    When trying to find the class of an object
	 *                                   that does not match/exist
	 */
	public synchronized void addTransaction(SignedObject signedTransaction) throws InvalidKeyException, UnrecoverableKeyException,
			SignatureException, KeyStoreException, NoSuchAlgorithmException, IOException, ClassNotFoundException {
		if (currentBlock == null)
			currentBlock = new Block(new byte[32], 0);

		if (currentBlock.getNumTransactions() == 5) { // New block

			long blockId = currentBlock.getBlockId() + 1;
			byte[] hash = currentBlock.getHash();
			currentBlock = new Block(hash, blockId);
		}
		
		currentBlock.addTransaction(signedTransaction);
		
		if (currentBlock.getNumTransactions() == 5) {
			currentBlock.signBlock();
			currentBlock.saveBlockToFile();
		}
	}

	/**
	 * Gets the last block from the log folder
	 * 
	 * @return The last block from log folder
	 * @throws FileIntegrityViolationException If the loaded file is corrupted
	 */
	private Block findLastBlock() throws FileIntegrityViolationException {
		File folder = new File(LOGS_FOLDER);
		File[] files = folder.listFiles((dir, name) -> name.endsWith(".blk"));

		if (files == null || files.length == 0)
			return null;

		int maxId = -1;
		String maxFileName = "";
		for (File file : files) {
			String fileName = file.getName();
			int id = Integer.parseInt(fileName.substring(fileName.indexOf("_") + 1, fileName.indexOf(".blk")));
			if (id > maxId) {
				maxId = id;
				maxFileName = fileName;
			}
		}
		return readBlockFromFile(maxFileName, maxId);
	}

	/**
	 * Verifies the integrity of the blockChain
	 * 
	 * @return True if the blockChain is not corrupted, false otherwise
	 * @throws FileIntegrityViolationException If the loaded file is corrupted
	 * @throws InvalidKeyException             If the key is invalid
	 * @throws UnrecoverableKeyException       If the key cannot be recovered
	 * @throws SignatureException              When an error occurs while signing an
	 *                                         object
	 * @throws KeyStoreException               If an exception occurs while
	 *                                         accessing the keystore
	 * @throws NoSuchAlgorithmException        If the requested algorithm is not
	 *                                         available
	 * @throws ClassNotFoundException          When trying to find the class of an
	 *                                         object that does not match/exist
	 * @throws IOException                     When inStream does not receive input
	 *                                         or the outStream can't send the
	 *                                         result message
	 */
	public synchronized boolean verifyBlockchainIntegrity()
			throws FileIntegrityViolationException, InvalidKeyException, UnrecoverableKeyException, SignatureException,
			KeyStoreException, NoSuchAlgorithmException, ClassNotFoundException, IOException {

		File folder = new File(LOGS_FOLDER);
		File[] files = folder.listFiles((dir, name) -> name.endsWith(".blk"));
		Arrays.sort(files); //If the sorting strategy of files changes, this whole thing breaks
		
		if (files == null || files.length == 0)
			return true;

		long count = 0;
		Block lastBlock = null;
		for (File file : files) {
			Block block = readBlockFromFile(file.getName(), count);
			
			if (count != 0) {
				
				if(!MessageDigest.isEqual(block.getPreviousHash(), lastBlock.getHash())) {
					return false;
				}
				
			}
			
			lastBlock = block;
			count++;
		}
		return true;
	}

	/**
	 * Reads a block file
	 * 
	 * @param fileName The file name of the block to read
	 * @param blockId  The block id
	 * @return The block object
	 * @throws FileIntegrityViolationException If the loaded file is corrupted
	 */
	public synchronized Block readBlockFromFile(String fileName, long blockId) throws FileIntegrityViolationException {
		Block block = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(LOGS_FOLDER + "/" + fileName));
			String line;
			byte[] previousHash = null;
			long numTransactions = 0;
			ArrayList<String> transactions = new ArrayList<String>();
			String blockSignature = null;
			
			String hashString = reader.readLine();
			
			previousHash = parseByteString(hashString);
			
			
			try {
				numTransactions = Long.parseLong(reader.readLine());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < numTransactions; i++) {
				line = reader.readLine();
				
				if (line != null) {
					
					transactions.add(line + EOL);
					
					byte[] signedObjectBytes = parseByteString(line);
					
					ByteArrayInputStream in = new ByteArrayInputStream(signedObjectBytes);
					ObjectInputStream is = new ObjectInputStream(in);
					
					SignedObject signedTransaction = (SignedObject) is.readObject();
					
					Transaction t = (Transaction) signedTransaction.getObject();
					
					boolean isValidTransaction = signedTransaction.verify(UserCatalog.getInstance().getUserCertificate(t.getUid()).getPublicKey(), Signature.getInstance("MD5withRSA"));
					
					assert(isValidTransaction);
					
				}
				
				else
					throw new FileIntegrityViolationException("Blockchain integrity was corrupted!");
			}
			
			blockSignature = reader.readLine();
			
			byte[] signatureBytes = parseByteString(blockSignature);
			
			if (reader.readLine() != null)
				throw new FileIntegrityViolationException("Blockchain integrity was corrupted!");
			
			reader.close();

			block = new Block(previousHash, blockId);
			block.numTransactions = numTransactions;
			block.transactions = transactions;
			block.blockSignature = signatureBytes;
			
			assert(block.verifySignature(this.ks.getCertificate(alias).getPublicKey(), signatureBytes));
			
			reader.close();
		} catch (IOException e) {
			throw new FileIntegrityViolationException("Blockchain integrity was corrupted!");
		} catch (ClassNotFoundException e) {
			throw new FileIntegrityViolationException("Blockchain integrity was corrupted!");
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e ) {
			throw new FileIntegrityViolationException("Blockchain integrity was corrupted!");
		}
		
		
		return block;
	}

	/**
	 * Sets the KeyStore to the given Keystore
	 * 
	 * @param keystore The KeyStore to set to
	 * @param alias    The entry identifier of the given KeyStore
	 * @param pwd      The password for the given KeyStore
	 */
	public void setKeyStore(KeyStore keystore, String alias, String pwd) {
		this.ks = keystore;
		this.alias = alias;
		this.pwd = pwd;
	}

	/**
	 * Returns the unique instance of the LogUtils class
	 * 
	 * @return The unique instance of this class
	 */
	public static LogUtils getInstance() {
		if (instance == null)
			instance = new LogUtils();
		return instance;
	}
	
	public byte[] parseByteString(String byteStr) throws NumberFormatException{
		
		String byteStrTrimmed = byteStr.trim();
		
		String[] stringValues = byteStrTrimmed.substring(1, byteStrTrimmed.length() - 1).split(",\\s*");
		byte[] byteArray = new byte[stringValues.length];
		
		for (int i = 0; i < byteArray.length; i++) {
			
			byteArray[i] = (byte) Integer.parseInt(stringValues[i]);
			
		}
		
		return byteArray;
	}

	/**
	 * The Block class has some functions that are often used in this project (block
	 * management).
	 * 
	 * @author André Dias nº 55314
	 * @author David Pereira nº 56361
	 * @author Miguel Cut nº 56339
	 */
	public class Block implements Serializable {

		private static final long serialVersionUID = 1L;
		private byte[] previousHash;
		private long blockId;
		private long numTransactions;
		private ArrayList<String> transactions;
		private byte[] blockSignature;

		/**
		 * This constructor creates a new Block given its id and the hash of the
		 * previous block
		 * 
		 * @param previousHash The hash of the previous block
		 * @param blockId      The block id (this block)
		 */
		public Block(byte[] previousHash, long blockId) {
			this.previousHash = previousHash;
			this.blockId = blockId;
			this.numTransactions = 0;
			this.transactions = new ArrayList<>();
			this.blockSignature = new byte[32];
		}
		
		public boolean verifySignature(PublicKey pk, byte[] signature) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {
			
			Signature s = Signature.getInstance("SHA256withRSA");
			s.initVerify(pk);

			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bs);
			
			os.writeObject(this.previousHash);
			os.writeObject(this.blockId);
			os.writeObject(this.numTransactions);
			os.writeObject(this.transactions);

			os.flush();
			os.close();

			byte[] signedBlockBytes = bs.toByteArray();
			
			s.update(signedBlockBytes);
			
			return s.verify(signature);
			
		}

		/**
		 * Signs the block
		 * 
		 * @throws InvalidKeyException       If the key is invalid
		 * @throws UnrecoverableKeyException If the key cannot be recovered
		 * @throws SignatureException        When an error occurs while signing an
		 *                                   object
		 * @throws KeyStoreException         If an exception occurs while accessing the
		 *                                   keystore
		 * @throws NoSuchAlgorithmException  If the requested algorithm is not available
		 * @throws IOException               When inStream does not receive input or the
		 *                                   outStream can't send the result message
		 * @throws ClassNotFoundException    When trying to find the class of an object
		 *                                   that does not match/exist
		 */
		public synchronized void signBlock() throws InvalidKeyException, UnrecoverableKeyException,
				SignatureException, KeyStoreException, NoSuchAlgorithmException, IOException, ClassNotFoundException {

			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bs);
			
			os.writeObject(this.previousHash);
			os.writeObject(this.blockId);
			os.writeObject(this.numTransactions);
			os.writeObject(this.transactions);

			os.flush();
			os.close();

			byte[] signedBlockBytes = bs.toByteArray();
			
			Signature s = Signature.getInstance("SHA256withRSA");
			s.initSign((PrivateKey) ks.getKey(alias, pwd.toCharArray()));
			s.update(signedBlockBytes);

			this.blockSignature = s.sign();
			
		}

		/**
		 * Saves this block to the blockChain
		 */
		public synchronized void saveBlockToFile() {
			try {
				FileWriter writer = new FileWriter(LOGS_FOLDER + "/block_" + blockId + ".blk");
				FileOutputStream fos = new FileOutputStream(LOGS_FOLDER + "/block_" + blockId + ".blk");
				
				System.out.println("Saving .blk...");
				
				fos.write((Arrays.toString(getPreviousHash()) + EOL).getBytes());
				
				fos.write(new String(numTransactions + EOL).getBytes());
				
				for (int i = 0; i < numTransactions; i++) {
					fos.write(transactions.get(i).getBytes());
				}
				
				fos.write(Arrays.toString(this.blockSignature).getBytes());
				
				writer.close();
				fos.close();

			} catch (IOException e) {
				System.out.println("Error saving block.");
				e.printStackTrace();
			}
		}

		/**
		 * Add a transaction to this block
		 * 
		 * @param transaction The transaction to be added
		 * @throws IOException 
		 * @throws NoSuchAlgorithmException 
		 * @throws KeyStoreException 
		 * @throws SignatureException 
		 * @throws UnrecoverableKeyException 
		 * @throws InvalidKeyException 
		 */
		public synchronized void addTransaction(SignedObject signedTransaction) throws InvalidKeyException, UnrecoverableKeyException, SignatureException, KeyStoreException, NoSuchAlgorithmException, IOException {
			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bout); 
			
			os.writeObject(signedTransaction);
			
			byte[] signedTransactionBytes = bout.toByteArray();
	
			transactions.add(Arrays.toString(signedTransactionBytes) + EOL);
			numTransactions++;
			saveBlockToFile();
		}

		/**
		 * Returns the id of this block
		 * 
		 * @return The id of this block
		 */
		public long getBlockId() {
			return blockId;
		}

		/**
		 * Returns the hash of this block
		 * 
		 * @return The hash of this block
		 * @throws NoSuchAlgorithmException
		 * @throws IOException
		 */
		public synchronized byte[] getHash() throws NoSuchAlgorithmException, IOException {

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bs);

			os.writeObject(this.previousHash);
			os.writeObject(this.blockId);
			os.writeObject(this.numTransactions);
			os.writeObject(this.transactions);
			os.writeObject(this.blockSignature);

			os.flush();
			os.close();

			byte[] blockBytes = bs.toByteArray();

			md.reset();
			md.update(blockBytes);
			
			byte[] hash = md.digest();

			return hash;

		}

		/**
		 * Returns the number of transactions saved in this block
		 * 
		 * @return The number of transactions saved in this block
		 */
		public long getNumTransactions() {
			return numTransactions;
		}

		/**
		 * Returns a list with the transactions saved in this block
		 * 
		 * @return A list with the transactions saved in this block
		 */
		public ArrayList<String> getTransactions() {
			return transactions;
		}

		/**
		 * Returns the hash of the previous block
		 * 
		 * @return The hash of the previous block
		 */
		public byte[] getPreviousHash() {
			return previousHash;
		}
	}
}
