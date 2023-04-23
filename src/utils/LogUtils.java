package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
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
import java.util.Arrays;

public class LogUtils implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8914479604836760464L;
	private static final String LOGS_FOLDER = "server_files/logs";
	private static final String LOG_FILE = LOGS_FOLDER + "/log.txt";
	private static LogUtils instance = null;
	private File log;
    private Block currentBlock;
    private transient KeyStore ks;
    private String alias;
    private String pwd;
	
	private LogUtils() throws IOException {
	    File directory = new File(LOGS_FOLDER);
	    if (! directory.exists()){
	        directory.mkdir();
	    }
		log = new File(LOG_FILE);
		currentBlock = findLastBlock();
	}
	
	public synchronized void writeSale(String wine, double value, int quantity, String seller) throws InvalidKeyException, UnrecoverableKeyException, SignatureException, KeyStoreException, NoSuchAlgorithmException, IOException {
		String transaction = "sale: " + wine + " " + value + " " + quantity + " " + seller + "\n";
		BufferedWriter bw = new BufferedWriter(new FileWriter(log, true));
		bw.append(transaction);
		bw.close();
		addTransaction(transaction);
	}
	
	public synchronized void writeBuy(String wine, double value, int quantity, String buyer) throws InvalidKeyException, UnrecoverableKeyException, SignatureException, KeyStoreException, NoSuchAlgorithmException, IOException {
		String transaction = "buy: " + wine + " " + value + " " + quantity + " " + buyer + "\n";
		BufferedWriter bw = new BufferedWriter(new FileWriter(log, true));
		bw.append(transaction);
		bw.close();
		addTransaction(transaction);
	}
	
	private synchronized void addTransaction(String transaction) throws InvalidKeyException, UnrecoverableKeyException, SignatureException, KeyStoreException, NoSuchAlgorithmException, IOException {
		if(currentBlock == null) {
			currentBlock = new Block(new byte[32], 0);
		}

		if(currentBlock.getNumTransactions() == 5) { //New block
			
			long blockId = currentBlock.getBlockId() + 1;
			byte[] hash = currentBlock.getHash();
			currentBlock = new Block(hash, blockId);
		}
		currentBlock.addTransaction(transaction);
		if(currentBlock.getNumTransactions() == 5)
			currentBlock.calculateBlockHash();
	}
	
	private Block findLastBlock() {
        File folder = new File(LOGS_FOLDER); // replace with the path to your folder

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".blk")); // get all .blk files
        
        if (files == null || files.length == 0) {
            return null;
        }
        
        int maxId = -1;
        String maxFileName = "";
        for (File file : files) {
            String fileName = file.getName();
            int id = Integer.parseInt(fileName.substring(fileName.indexOf("_")+1, fileName.indexOf(".blk")));
            if (id > maxId) {
                maxId = id;
                maxFileName = fileName;
            }
        }
        return readBlockFromFile(maxFileName, maxId);
	}
	
	public boolean verifyBlockchainIntegrity() {
        File folder = new File(LOGS_FOLDER); // replace with the path to your folder

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".blk")); // get all .blk files
        
        if (files == null || files.length == 0) {
            return true;
        }
        
        long count = 0;
        Block lastBlock = null;
        for (File file : files) {
        	Block block = readBlockFromFile(file.getName(), count);

        	if(count != 0 && !Arrays.equals(block.previousHash, lastBlock.getHash())) 
        		return false;
        	lastBlock = block;
        	count++;
        }
        return true;
	}
	
    public Block readBlockFromFile(String fileName, long blockId) {
        Block block = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(LOGS_FOLDER + "/" + fileName));
            String line;
            byte[] previousHash = null;
            long numTransactions = 0;
            ArrayList<String> transactions = new ArrayList<String>();
            byte[] blockHash = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Previous Hash: ")) {
                    previousHash = line.substring("Previous Hash: ".length()).getBytes();
                } else if (line.startsWith("Number of Transactions: ")) {
                    numTransactions = Long.parseLong(line.substring("Number of Transactions: ".length()));
                } else if (line.startsWith("Transactions: ")) {
                    String transactionsString = line.substring("Transactions: ".length());
                    transactions.add(transactionsString.replace("[", "") + "\n");
                    for(int i = 0; i < numTransactions-1; i++) {
                    	transactions.add(reader.readLine().substring(2) + "\n");
                    }
                } else if (line.startsWith("Block Hash: ")) {
                    blockHash = line.substring("Block Hash: ".length()).getBytes();
                }
            }
            reader.close();
            block = new Block(previousHash, blockId);
            block.numTransactions = numTransactions;
            block.transactions = transactions;
            block.blockHash = blockHash;
        } catch (IOException e) {
            System.out.println("Error reading block from file.");
            e.printStackTrace();
        }
        return block;
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
			
			SignedObject signedBlock = new SignedObject(this,(PrivateKey) ks.getKey(alias, pwd.toCharArray()), Signature.getInstance("MD5withRSA"));
	    	MessageDigest md = MessageDigest.getInstance("SHA-256");

			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bs);

			os.writeObject(signedBlock);
			os.flush();
			os.close();
			byte[] signedBlockBytes = bs.toByteArray();
			byte[] hash = md.digest(signedBlockBytes);
			byte[] truncatedHash = new byte[32];
			System.arraycopy(hash, 0, truncatedHash, 0, 32);
			this.blockHash = truncatedHash;
			saveBlockToFile();
	    }
	    
	    public void saveBlockToFile() {
	        try {
	            FileWriter writer = new FileWriter("server_files/logs/block_" + blockId + ".blk");
	            writer.write("Previous Hash: " + new String(previousHash) + "\n");
	            writer.write("Number of Transactions: " + numTransactions + "\n");
	            writer.write("Transactions: " + transactions.toString() + "\n");
	            writer.write("Block Hash: " + new String(blockHash) + "\n");
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
	    
	    public long getNumTransactions() {
			return numTransactions;
		}
	    
	}

	
}
