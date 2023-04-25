package handlers;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import utils.FileIntegrityViolationException;
import utils.LogUtils;
import utils.LogUtils.Block;

public class ListHandler {

	private static ListHandler instance = null;

	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream) throws Exception {
		File folder = new File("server_files/logs");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".blk"));
        
        if (files == null || files.length == 0) {
        	outStream.writeObject("Failed to read block files"); 
        	return;
        }
        
        long count = 0;
        Block lastBlock = null;
        if(!LogUtils.getInstance().verifyBlockchainIntegrity()) {
        	outStream.writeObject("The blockchain was corrupted"); 
        	return;
        }
        	
        StringBuilder sb = new StringBuilder();
        
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
	
	public static ListHandler getInstance() {
		if (instance == null) 
			instance = new ListHandler();
		return instance;
	}
}
