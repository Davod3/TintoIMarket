package domain;

import java.io.Serializable;

public abstract class Transaction implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8531862903135878060L;
	private String uid;
	private String wineid;
	private String type;
	
	protected Transaction(String uid, String wineid, String type) {
		this.uid = uid;
		this.wineid = wineid;
		this.type = type;
	}
	
	public String getUid() {
		return this.uid;
	}
	
	public String getWineid() {
		return this.wineid;
	}
	
	public String getType() {
		return this.type;
	}

}
