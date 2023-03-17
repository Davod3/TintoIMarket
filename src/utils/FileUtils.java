package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * The FileUtils class has some functions
 * and strings that are often used in this project
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class FileUtils {

	public static final String EOL = System.lineSeparator();
	
	/**
	 * Sends a file through a given stream
	 * 
	 * @param fileName		The filename
	 * @param outStream		Stream for outputting the file
	 */
	public static void sendFile(String fileName, ObjectOutputStream outStream) {
		//Create file with filename
		File file = new File(fileName);
		//Get file size
		long size = file.length();
		//Attempt to send file
		try {
			outStream.writeObject(fileName);
			byte[] buffer = new byte[(int) size];
	        outStream.writeObject(buffer.length);
	        FileInputStream fin = new FileInputStream(file);
	        fin.read(buffer);
	        outStream.write(buffer, 0, buffer.length);
	        outStream.flush();
	        fin.close();
		} catch (IOException e) {
			//Send error message
			System.out.println("Error sending file: " + fileName);
		}
	}
	
	/**
	 * Receives a file from a given stream
	 * 
	 * @param inStream					Stream for receiving the file
	 * @return							The file received
	 * @throws ClassNotFoundException	When trying to find the class of an object
	 * 									that does not match/exist
	 * @throws IOException				When inStream does not receive input			
	 */
	public static File receiveFile(ObjectInputStream inStream) throws ClassNotFoundException, IOException {
		//Get filename
		String name = (String) inStream.readObject();
		//Get file size
		int size = (Integer) inStream.readObject();
		//Allocate space for file
		byte[] bytes = new byte[size];
		//Read file into space allocated
		inStream.readFully(bytes, 0, size);
		//Create a new file
		File outFile = new File(name);
		FileOutputStream fout = new FileOutputStream(outFile);
		//Write data into file
		fout.write(bytes);
		fout.close();
		return outFile;
	}
}
