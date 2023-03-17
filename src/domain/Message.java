package domain;

/**
 * The Message class represents a message in this application.
 * Each message has a sender, a receiver and the message itself.
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class Message {
	
	private String from;
	private String to;
	private String content;
	
	/**
	 * Creates a new Message given the sender, the receiver and the message
	 * 
	 * @param from			The sender
	 * @param to			The receiver
	 * @param content		The message
	 */
	public Message(String from, String to, String content) {
		this.from = from;
		this.to = to;
		this.content = content;
	}
	
	/**
	 * Returns the content of this message
	 * 
	 * @return	The content of the message
	 */
	public String getContent() {
		return content;
	}
	
	/**
	 * Returns the sender of this message
	 * 
	 * @return	The sender of the message
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * Returns the receiver of this message
	 * 
	 * @return	The receiver of the message
	 */
	public String getTo() {
		return to;
	}
}
