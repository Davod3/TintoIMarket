package domain;

public class Message {
	
	private String from;
	private String to;
	private String content;
	
	public Message(String from, String to, String content) {
		this.from = from;
		this.to = to;
		this.content = content;
	}
}
