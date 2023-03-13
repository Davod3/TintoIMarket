package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileUtils {

	
	public static void sendFile(String fileName, ObjectOutputStream outStream) {
		File file = new File(fileName);
		long size = file.length();
		try {
			outStream.writeObject(fileName);
			byte[] buffer = new byte[(int) size];
	        outStream.writeObject(buffer.length);
	        System.out.println();
	        FileInputStream fin = new FileInputStream(file);
	        fin.read(buffer);
	        outStream.write(buffer, 0, buffer.length);
	        outStream.flush();
	        fin.close();
		} catch (IOException e) {
			System.out.println("Error sending file: " + fileName);
		}
	}
	
	public static File receiveFile(ObjectInputStream inStream) throws ClassNotFoundException, IOException {
		
		String name = (String) inStream.readObject();
		int size = (Integer) inStream.readObject();
		
		byte[] bytes = new byte[size];
		
		inStream.readFully(bytes, 0, size);
		
		File outFile = new File(name);
		
		FileOutputStream fout = new FileOutputStream(outFile);
		fout.write(bytes);
		fout.close();
		
		return outFile;
	}
}
