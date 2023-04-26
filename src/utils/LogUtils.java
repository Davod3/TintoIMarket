package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.Arrays;

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
	private static final String LOG_FILE = LOGS_FOLDER + "/log.txt";
	private static LogUtils instance = null;
	private File log;
	private Block currentBlock;
	private transient KeyStore ks;
	private String alias;
	private String pwd;

	/**
	 * This constructor loads the transactions from the log file
	 */
	private LogUtils() {
		File directory = new File(LOGS_FOLDER);
		if (!directory.exists())
			directory.mkdir();

		log = new File(LOG_FILE);
		try {
			currentBlock = findLastBlock();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes a new sale into the log file and the blockChain
	 * 
	 * @param wine     The wine to put on sale
	 * @param value    The value for each unit of this wine
	 * @param quantity The quantity of this wine to put on sale
	 * @param seller   The seller of the wine
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
	public synchronized void writeSale(String wine, double value, int quantity, String seller)
			throws InvalidKeyException, UnrecoverableKeyException, SignatureException, KeyStoreException,
			NoSuchAlgorithmException, IOException, ClassNotFoundException {
		String transaction = "sale: " + wine + " " + value + " " + quantity + " " + seller + "\n";
		BufferedWriter bw = new BufferedWriter(new FileWriter(log, true));
		bw.append(transaction);
		bw.close();
		addTransaction(transaction);
	}

	/**
	 * Writes a new buy into the log file and the blockChain
	 * 
	 * @param wine     The wine to buy
	 * @param value    The value for each unit of this wine
	 * @param quantity The quantity of this wine to buy
	 * @param buyer    The buyer of the wine
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
	public synchronized void writeBuy(String wine, double value, int quantity, String buyer)
			throws InvalidKeyException, UnrecoverableKeyException, SignatureException, KeyStoreException,
			NoSuchAlgorithmException, IOException, ClassNotFoundException {
		String transaction = "buy: " + wine + " " + value + " " + quantity + " " + buyer + "\n";
		BufferedWriter bw = new BufferedWriter(new FileWriter(log, true));
		bw.append(transaction);
		bw.close();
		addTransaction(transaction);
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
	private synchronized void addTransaction(String transaction) throws InvalidKeyException, UnrecoverableKeyException,
			SignatureException, KeyStoreException, NoSuchAlgorithmException, IOException, ClassNotFoundException {
		if (currentBlock == null)
			currentBlock = new Block(new byte[32], 0);

		if (currentBlock.getNumTransactions() == 5) { // New block

			long blockId = currentBlock.getBlockId() + 1;
			byte[] hash = currentBlock.getHash();
			currentBlock = new Block(hash, blockId);
		}
		currentBlock.addTransaction(transaction);
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
	public boolean verifyBlockchainIntegrity()
			throws FileIntegrityViolationException, InvalidKeyException, UnrecoverableKeyException, SignatureException,
			KeyStoreException, NoSuchAlgorithmException, ClassNotFoundException, IOException {

		File folder = new File(LOGS_FOLDER);
		File[] files = folder.listFiles((dir, name) -> name.endsWith(".blk"));

		if (files == null || files.length == 0)
			return true;

		long count = 0;
		Block lastBlock = null;
		for (File file : files) {
			Block block = readBlockFromFile(file.getName(), count);
			
			if (count != 0) {
				
				System.out.println("Supposed hash of the previous block: " + new String(block.getPreviousHash()));
				System.out.println("Actual hash of the previous block: " + new String(lastBlock.getHash()));
				
				if (!new String(block.getPreviousHash()).equals(new String(lastBlock.getHash()))) {
					return false;
				}
				
				/*
				lastBlock.calculateBlockHash();

				if (!new String(block.getPreviousHash()).equals(new String(lastBlock.getHash()))) {
					return false;
				}
				*/
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
	public Block readBlockFromFile(String fileName, long blockId) throws FileIntegrityViolationException {
		Block block = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(LOGS_FOLDER + "/" + fileName));
			String line;
			byte[] previousHash = null;
			long numTransactions = 0;
			ArrayList<String> transactions = new ArrayList<String>();
			byte[] blockSignature = null;
			
			String hashString = reader.readLine();
			previousHash = hashString.getBytes(StandardCharsets.UTF_8);
			
			//previousHash = reader.readLine().getBytes();
			try {
				numTransactions = Long.parseLong(reader.readLine());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < numTransactions; i++) {
				line = reader.readLine();
				if (line != null)
					transactions.add(line + "\n");
				else
					throw new FileIntegrityViolationException("Blockchain integrity was violated!");
			}
			blockSignature = reader.readLine().getBytes();
			if (reader.readLine() != null)
				throw new FileIntegrityViolationException("Blockchain integrity was violated!");
			reader.close();

			block = new Block(previousHash, blockId);
			block.numTransactions = numTransactions;
			block.transactions = transactions;
			block.blockSignature = blockSignature;
			System.out.println(block.blockId + "-This is the previous hash read from file: " + hashString);
			
			reader.close();
		} catch (IOException e) {
			System.out.println("Error reading block from file.");
			e.printStackTrace();
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

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bs);
			
			os.writeObject(this.previousHash);
			os.writeObject(this.blockId);
			os.writeObject(this.numTransactions);
			os.writeObject(this.transactions);

			os.flush();
			os.close();

			byte[] signedBlockBytes = bs.toByteArray();

			SignedObject signedBlock = new SignedObject(signedBlockBytes,
					(PrivateKey) ks.getKey(alias, pwd.toCharArray()), Signature.getInstance("SHA256withRSA"));

			md.reset();
			md.update(signedBlockBytes);

			byte[] hash = signedBlock.getSignature();
			String hashStr = Arrays.toString(hash);
			this.blockSignature = hashStr.getBytes();
			
		}

		/**
		 * Saves this block to the blockChain
		 */
		public void saveBlockToFile() {
			try {
				FileWriter writer = new FileWriter(LOGS_FOLDER + "/block_" + blockId + ".blk");
				FileOutputStream fos = new FileOutputStream(LOGS_FOLDER + "/block_" + blockId + ".blk");

				fos.write((new String(getPreviousHash()) + "\n").getBytes());
				fos.write(new String(numTransactions + "\n").getBytes());
				for (int i = 0; i < numTransactions; i++) {
					byte[] bytes = new String(transactions.get(i)).getBytes();
					fos.write(bytes);
				}
				fos.write(blockSignature);

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
		 */
		public void addTransaction(String transaction) {
			transactions.add(transaction);
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
		public byte[] getHash() throws NoSuchAlgorithmException, IOException {

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
			String hashStr = Arrays.toString(hash);

			return hashStr.getBytes();

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
