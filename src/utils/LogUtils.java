package utils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
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

public class LogUtils implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8914479604836760464L;
	private static LogUtils instance = null;
	private File log;
    private transient PrintWriter pw;
    private int count;
    private Block currentBlock;
    private transient KeyStore ks;
    private String alias;
    private String pwd;
	
	private LogUtils() throws IOException {
	    File directory = new File("server_files/logs");
	    if (! directory.exists()){
	        directory.mkdir();
	        // If you require it to make the entire directory path including parents,
	        // use directory.mkdirs(); here instead.
	    }
		log = new File("server_files/logs/log.txt");
		pw = new PrintWriter(new FileWriter(log));
		currentBlock = null;
		count = 0;
	}
	
	public synchronized void writeSale(String wine, double value, int quantity, String seller) throws InvalidKeyException, UnrecoverableKeyException, SignatureException, KeyStoreException, NoSuchAlgorithmException, IOException {
		String transaction = "sale: " + wine + " " + value + " " + quantity + " " + seller + "\n";
		BufferedWriter bw = new BufferedWriter(new FileWriter(log, true));
		bw.append(transaction);
		bw.close();
		manageCount(transaction);
	}
	
	public synchronized void writeBuy(String wine, double value, int quantity, String buyer) throws InvalidKeyException, UnrecoverableKeyException, SignatureException, KeyStoreException, NoSuchAlgorithmException, IOException {
		String transaction = "buy: " + wine + " " + value + " " + quantity + " " + buyer + "\n";
		pw.print(transaction);
		manageCount(transaction);
	}
	
	private synchronized void manageCount(String transaction) throws InvalidKeyException, UnrecoverableKeyException, SignatureException, KeyStoreException, NoSuchAlgorithmException, IOException {
		count++;
		if(currentBlock == null) {
			currentBlock = new Block(new byte[32], 0);
			System.out.println("FIRST BLOCK");
		}
		currentBlock.addTransaction(transaction);

		if(count > 4) {	//New block
			System.out.println("FINISHED BLOCK");
			currentBlock.calculateBlockHash();
			System.out.println("HEREE0");
			currentBlock.saveBlockToFile();
			System.out.println("HEREE1");

			count = 0;
			long blockId = currentBlock.getBlockId() + 1;
			byte[] hash = currentBlock.getHash();
			System.out.println("HEREE2");
			currentBlock = new Block(hash, blockId);
			System.out.println("HEREE3");
		}
	}
	
	public void setKeyStore(KeyStore keystore, String alias, String pwd) {
		this.ks = keystore;
		this.alias = alias;
		this.pwd = pwd;
	}
	
	public static LogUtils getInstance() throws IOException {
		if (instance == null)
			instance = new LogUtils();
		return instance;
	}

	public class Block implements Serializable {
		
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private byte[] previousHash;
	    private long blockId;
	    private long numTransactions;
	    private ArrayList<String> transactions;
	    private byte[] blockHash;

	    public Block(byte[] previousHash, long blockId) {
	        this.previousHash = previousHash;
	        this.blockId = blockId;
	        this.numTransactions = 0;
	        this.transactions = new ArrayList<String>();
	        this.blockHash = new byte[32];
	    }
	    
	    public synchronized void calculateBlockHash() throws InvalidKeyException, UnrecoverableKeyException, SignatureException, KeyStoreException, NoSuchAlgorithmException, IOException {
			System.out.println("HELLOOO");
			
			SignedObject signedBlock = new SignedObject(this,(PrivateKey) ks.getKey(alias, pwd.toCharArray()), Signature.getInstance("MD5withRSA"));
			System.out.println("HELLOOO1");
	    	MessageDigest md = MessageDigest.getInstance("SHA-256");

			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bs);
			System.out.println("HELLOOO2");

			os.writeObject(signedBlock);
			os.flush();
			os.close();
			System.out.println("HELLOOO3");
			byte[] signedBlockBytes = bs.toByteArray();
			byte[] hash = md.digest(signedBlockBytes);
			byte[] truncatedHash = new byte[32];
			System.arraycopy(hash, 0, truncatedHash, 0, 32);
			this.blockHash = truncatedHash;
	    }
	    
	    public void saveBlockToFile() {
	        try {
	            FileWriter writer = new FileWriter("server_files/logs/block_" + blockId + ".blk");
	            writer.write("Previous Hash: " + previousHash + "\n");
	            writer.write("Number of Transactions: " + numTransactions + "\n");
	            writer.write("Transactions: " + transactions.toString() + "\n");
	            writer.write("Block Hash: " + blockHash + "\n");
	            writer.close();
	        } catch (IOException e) {
	            System.out.println("Error saving block.");
	            e.printStackTrace();
	        }
	    }
	    
	    public void addTransaction(String transaction){
	    	transactions.add(transaction);
	    	numTransactions++;
	    	saveBlockToFile();
	    }
	    
	    public long getBlockId() {
	    	return blockId;
	    }
	    
	    public byte[] getHash() {
	    	return blockHash;
	    }
	    
	}

	
}
