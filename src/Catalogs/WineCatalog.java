package Catalogs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import domain.Sale;
import domain.Wine;

public class WineCatalog {
	
	
	private static WineCatalog instance = null;
	private Map<String, Wine> wineList;
	
	
	private WineCatalog() {
		
		wineList = new HashMap<String, Wine>();
		
	}
	
	public static WineCatalog getInstance() {
		
		if (instance == null)
			instance = new WineCatalog();
		return instance;
		
	}

	public boolean createWine(String wine, File received, String author) {
		
		Wine newWine = new Wine(wine, received, author);
		
		if(!wineList.containsKey(wine)) {
			wineList.put(wine, newWine);
			return true;
		} else {
			return false;
		}
		
	}
	
	public void rate(String wine, int rating) {
		Wine wineToRate = getWine(wine);
		wineToRate.setRating(calculateRating(wine, rating));
		wineToRate.setNumberOfReviews(wineToRate.getNumberOfReviews()+1);
	}
	
	private double calculateRating(String wine, int rating) {
		double result = 0;
		Wine wineToRate = getWine(wine);
		result = wineToRate.getRating() * wineToRate.getNumberOfReviews() + rating;
		result /= wineToRate.getNumberOfReviews() + 1;
		return result;
	}
	
	public boolean wineExists(String wine) {
		return wineList.containsKey(wine);
	}
	
	public Wine getWine(String wine) {
		return wineList.get(wine);
	}
}
