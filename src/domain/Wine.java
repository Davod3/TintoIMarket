package domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Wine {
	
	private String name;
	private File image;
	private double rating;
	private int numberOfReviews;
	private List<Sale> sales;
	
	public Wine(String name, File image) {
		this.name = name;
		this.image = image;
		this.rating = 0;
		sales = new ArrayList<Sale>();
	}
	
	public String getName() {
		return name;
	}
	
	public String getImageName() {
		return image.getName();
	}
	
	public synchronized List<Sale> getSales(){
		return sales;
	}
	
	public synchronized Sale getSaleBySeller(String seller) {
		for(Sale sale : sales) {
			if(sale.getSeller().equals(seller))
				return sale;
		}
		return null;
	}
	
	public synchronized void addSale(Sale sale) {
		sales.add(sale);
	}
	
	public synchronized void setRating(double rating) {
		this.rating = rating;
	}
	
	public synchronized double getRating() {
		return rating;
	}
	
	public synchronized int getNumberOfReviews() {
		return numberOfReviews;
	}
	
	public synchronized void setNumberOfReviews(int numberOfReviews) {
		this.numberOfReviews = numberOfReviews;
	}
	
	public synchronized boolean hasSales() {
		return !sales.isEmpty();
	}
}
