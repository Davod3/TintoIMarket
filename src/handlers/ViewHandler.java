package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ViewHandler {
	
	private static ViewHandler instance = null;
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser) {
		
	}
	
	public static ViewHandler getInstance() throws IOException {

		if (instance == null)
			instance = new ViewHandler();
		return instance;

	}
}
