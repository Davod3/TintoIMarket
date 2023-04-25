package handlers;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;

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
        		sb.append(block.getTransactions().get(i));
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
