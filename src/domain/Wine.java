package domain;

import java.io.File;

public class Wine {
	private String name;
	private File image;
	private int rating;
	private String author;
	private int stock;
	
	public Wine(String name, File image, String author) {
		this.name = name;
		this.image = image;
		this.author = author;
		this.rating = 0;
		this.stock = 0;
	}
	
	public void setWineStock(int stock) {
		this.stock = stock;
	}

	public int getWineStock() {
		return stock;
	}
}
