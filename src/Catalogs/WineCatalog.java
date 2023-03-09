package Catalogs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
	private static final String WINE_FILE_PATH = "wines.txt";
	private static final String SALES_FILE_PATH = "sales.txt";
	private static final String SEPARATOR = ":";
	
	
	private WineCatalog() throws IOException {
		
		wineList = loadWines();
		
	}
	
	public static WineCatalog getInstance() throws IOException {
		
		if (instance == null)
			instance = new WineCatalog();
		return instance;
		
	}
	
	private HashMap<String, Wine> loadWines() throws IOException {
		
		HashMap<String, Wine> map = new HashMap<String, Wine>();
		
		File wineFile = new File(WINE_FILE_PATH);
		wineFile.createNewFile(); //Make sure file exists before reading
		
		BufferedReader br = new BufferedReader(new FileReader(WINE_FILE_PATH));
		
		String line;
		
		while((line = br.readLine()) != null) {
			
			String[] splitData = line.split(SEPARATOR);
			Wine newWine = new Wine(splitData[0], new File(splitData[1]));
			newWine.setRating(Double.parseDouble(splitData[2]));
			newWine.setNumberOfReviews(Integer.parseInt(splitData[3]));
			
			loadSales(newWine);
			
			
		
		}
		
		return map;
		
	}
	
	private void loadSales(Wine wine) {
		
		
	}

	public boolean createWine(String wine, File received) {
		
		Wine newWine = new Wine(wine, received);
		
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
