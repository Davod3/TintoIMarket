package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TalkHandler {
	
	private static TalkHandler instance = null;
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser) {
		
	}
	
	public static TalkHandler getInstance() throws IOException {

		if (instance == null)
			instance = new TalkHandler();
		return instance;

	}
}
