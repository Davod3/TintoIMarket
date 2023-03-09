package domain;

import java.io.File;
import java.util.List;

public class Wine {
	
	private String name;
	private File image;
	private int rating;
	private int numberOfReviews;
	private List<Sale> sales;
	
	public Wine(String name, File image, String author) {
		this.name = name;
		this.image = image;
		this.rating = 0;
	}
	
	public List<Sale> getSales(){
		return sales;
	}
	
	public Sale getSaleBySeller(String seller) {
		for(Sale sale : sales) {
			if(sale.getSeller().equals(seller))
				return sale;
		}
		return null;
	}
	
	public void addSale(Sale sale) {
		sales.add(sale);
	}
	
	public void setRating(int rating) {
		this.rating = rating;
	}
	
	public int getRating() {
		return rating;
	}
	
	public int getNumberOfReviews() {
		return numberOfReviews;
	}
	
	public void setNumberOfReviews(int numberOfReviews) {
		this.numberOfReviews = numberOfReviews;
	}
	
	public boolean hasSales() {
		return !sales.isEmpty();
	}
}
