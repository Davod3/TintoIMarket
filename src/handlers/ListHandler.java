package handlers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.SignedObject;
import java.security.UnrecoverableKeyException;
import java.util.Arrays;

import domain.BuyTransaction;
import domain.SaleTransaction;
import domain.Transaction;
import utils.FileIntegrityViolationException;
import utils.LogUtils;
import utils.LogUtils.Block;

/**
 * The ListHandler class represents the action of getting the list of transactions. 
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class ListHandler {

	private static ListHandler instance = null;
	private static final String EOL = System.lineSeparator();

	/**
	 * Returns the list of transactions for the current user
	 * 
	 * @param inStream							Stream for receiving input
	 * @param outStream							Stream for outputting result
	 * @throws IOException 						When inStream does not receive input
	 * 											or the outStream can't send the result message
	 * @throws FileIntegrityViolationException 	If the loaded file's is corrupted
	 * @throws ClassNotFoundException 			When trying to find the class of an object
	 * 											that does not match/exist
	 * @throws NoSuchAlgorithmException 		If the requested algorithm is not available
	 * @throws KeyStoreException 				If an exception occurs while accessing the keystore
	 * @throws SignatureException 				When an error occurs while signing an object
	 * @throws UnrecoverableKeyException 		If the key cannot be recovered
	 * @throws InvalidKeyException 				If the key is invalid
	 */
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream)
			throws IOException, InvalidKeyException, UnrecoverableKeyException,
			SignatureException, KeyStoreException, NoSuchAlgorithmException,
			ClassNotFoundException, FileIntegrityViolationException {
		File folder = new File("server_files/logs");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".blk"));
        Arrays.sort(files);
        
        if (files == null || files.length == 0) {
        	outStream.writeObject("No transactions to show"); 
        	return;
        }
        
        if(!LogUtils.getInstance().verifyBlockchainIntegrity()) {
        	outStream.writeObject("The blockchain was corrupted"); 
        	return;
        }
        	
        StringBuilder sb = new StringBuilder();
        
        long count = 0;
        Block lastBlock = null;
        for (File file : files) {
        	Block block = LogUtils.getInstance().readBlockFromFile(file.getName(), count);
        	for(int i = 0; i < block.getNumTransactions(); i++) {
        		
        		byte[] signedTransactionBytes = LogUtils.getInstance().parseByteString(block.getTransactions().get(i));
        		
        		 ByteArrayInputStream in = new ByteArrayInputStream(signedTransactionBytes);
        		 ObjectInputStream is = new ObjectInputStream(in);
        		 
        		 SignedObject signedTransaction = (SignedObject) is.readObject();
        		 
        		 Transaction t = (Transaction) signedTransaction.getObject();
        		 
        		 if(t.getType().equals("sell")) {
        			 
        			 //sell transaction
        			 
        			 SaleTransaction st = (SaleTransaction) t;
        			 
        			 sb.append("Sale: " + st.getWineid() + " : " + st.getNumUnits() + " : " + st.getUnitValue() + " : " + st.getUid() + EOL);
        			 
        			 
        		 } else {
        			 
        			 //buy transaction
        			 
        			 BuyTransaction st = (BuyTransaction) t;
        			 
        			 sb.append("Buy: " + st.getWineid() + " : " + st.getUnitsSold() + " : " + st.getUnitValue() + " : " + st.getUid() + EOL);
        			 
        		 }
        		 
        	}
        	lastBlock = block;
        	count++;
        }
        outStream.writeObject(sb.toString());
	}
	
	/**
	 * Returns the unique instance of the ListHandler class.
	 * If there is no instance of the class, a new one is created and returned.
	 * 
	 * @return	the unique instance of the ListHandler class
	 */
	public static ListHandler getInstance() {
		if (instance == null) 
			instance = new ListHandler();
		return instance;
	}
}
