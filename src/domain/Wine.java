package domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The Wine class represents a wine in this application.
 * Each wine has a name, a image, a rating, a number of
 * reviews and a list of sales associated.
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class Wine {
	
	private String name;
	private File image;
	private double rating;
	private int numberOfReviews;
	private List<Sale> sales;
	
	/**
	 * Creates a new Wine given the name and image of the wine.
	 * 
	 * @param name		The name of the wine
	 * @param image		The image of the wine
	 */
	public Wine(String name, File image) {
		this.name = name;
		this.image = image;
		this.rating = 0;
		sales = new ArrayList<>();
	}
	
	/**
	 * Returns the name of this wine.
	 * 
	 * @return	The name of the wine
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the image name of this wine.
	 * 
	 * @return	The image name of the wine
	 */
	public String getImageName() {
		return image.getName();
	}
	
	/**
	 * Returns the list of sales of this wine.
	 * 
	 * @return	The list of sales of the wine
	 */
	public synchronized List<Sale> getSales(){
		return sales;
	}
	
	/**
	 * Returns the sale associated to the seller.
	 * 
	 * @param seller	The seller of this wine
	 * @return			The sale associated to the seller
	 */
	public synchronized Sale getSaleBySeller(String seller) {
		for(Sale sale : sales) {
			if(sale.getSeller().equals(seller))
				return sale;
		}
		return null;
	}
	
	/**
	 * Adds a new sale to this wine.
	 * 
	 * @param sale		The sale we want to add to the wine
	 */
	public synchronized void addSale(Sale sale) {
		sales.add(sale);
	}
	
	/**
	 * Sets a new rating for this wine.
	 * 
	 * @param rating	The new rating for this wine
	 */
	public synchronized void setRating(double rating) {
		this.rating = rating;
	}
	
	/**
	 * Returns the rating of this wine.
	 * 
	 * @return	The rating of the wine
	 */
	public synchronized double getRating() {
		return rating;
	}
	
	/**
	 * Returns the number of reviews for this wine.
	 * 
	 * @return	The number of reviews for this wine
	 */
	public synchronized int getNumberOfReviews() {
		return numberOfReviews;
	}
	
	/**
	 * Sets a new number of reviews for this wine.
	 * 
	 * @param numberOfReviews	The new number of reviews for this wine
	 */
	public synchronized void setNumberOfReviews(int numberOfReviews) {
		this.numberOfReviews = numberOfReviews;
	}
	
	/**
	 * Checks if this wine has sales.
	 * 
	 * @return	True if this wine has sales, false otherwise
	 */
	public synchronized boolean hasSales() {
		return !sales.isEmpty();
	}
}
