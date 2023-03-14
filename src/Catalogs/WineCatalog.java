package Catalogs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import domain.Sale;
import domain.Wine;

public class WineCatalog {

	private static WineCatalog instance = null;
	private Map<String, Wine> wineList;
	private static final String WINE_FILE_PATH = "wines.txt";
	private static final String SALES_FILE_PATH = "sales.txt";
	private static final String SEPARATOR = ":";
	private static final String EOL = System.lineSeparator();

	private WineCatalog() throws IOException {
		
		wineList = loadWines();
	}

	public static WineCatalog getInstance() throws IOException {

		if (instance == null)
			instance = new WineCatalog();
		return instance;
	}

	private synchronized HashMap<String, Wine> loadWines() throws IOException {

		HashMap<String, Wine> map = new HashMap<String, Wine>();

		File wineFile = new File(WINE_FILE_PATH);
		wineFile.createNewFile(); // Make sure file exists before reading

		BufferedReader br = new BufferedReader(new FileReader(WINE_FILE_PATH));

		String line;

		while ((line = br.readLine()) != null) {

			String[] splitData = line.split(SEPARATOR);
			Wine newWine = new Wine(splitData[0], new File(splitData[1]));
			newWine.setRating(Double.parseDouble(splitData[2]));
			newWine.setNumberOfReviews(Integer.parseInt(splitData[3]));

			loadSales(newWine);

			map.put(splitData[0], newWine);
		}
		br.close();
		return map;
	}

	private synchronized void loadSales(Wine wine) throws IOException {

		File saleFile = new File(SALES_FILE_PATH);
		saleFile.createNewFile();

		BufferedReader br = new BufferedReader(new FileReader(SALES_FILE_PATH));

		String line;

		while ((line = br.readLine()) != null) {

			String[] splitData = line.split(SEPARATOR);

			if (splitData[0].equals(wine.getName())) {

				Sale newSale = new Sale(splitData[1], Double.parseDouble(splitData[2]), Integer.parseInt(splitData[3]),
						splitData[0]);
				wine.addSale(newSale);
			}
		}
		br.close();
	}

	public synchronized void updateWines() throws IOException {

		Set<String> keys = wineList.keySet();
		StringBuilder sbWines = new StringBuilder();
		StringBuilder sbSales = new StringBuilder();

		for (String key : keys) {

			Wine wine = wineList.get(key);
			sbWines.append(wine.getName() + SEPARATOR + wine.getImageName() + SEPARATOR + wine.getRating() + SEPARATOR
					+ wine.getNumberOfReviews() + EOL);
			
			getSales(sbSales, wine);

		}
		
		BufferedWriter bwWines = new BufferedWriter(new FileWriter(WINE_FILE_PATH));
		bwWines.write(sbWines.toString());
		
		BufferedWriter bwSales = new BufferedWriter(new FileWriter(SALES_FILE_PATH));
		bwSales.write(sbSales.toString());
		
		bwWines.close();
		bwSales.close();
	}

	private synchronized void getSales(StringBuilder sb, Wine wine) {
		
		List<Sale> sales = wine.getSales();
		
		for(Sale sale : sales) {
			
			sb.append(sale.getWineName() + SEPARATOR + sale.getSeller() + SEPARATOR + 
					sale.getValue() + SEPARATOR +
					sale.getQuantity() + EOL);
		}
	}

	public synchronized boolean createWine(String wine, File received) throws IOException {

		Wine newWine = new Wine(wine, received);

		if (!wineList.containsKey(wine)) {
			wineList.put(wine, newWine);
			updateWines();
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized void rate(String wine, double rating) throws IOException {
		Wine wineToRate = getWine(wine);
		wineToRate.setRating(calculateRating(wine, rating));
		wineToRate.setNumberOfReviews(wineToRate.getNumberOfReviews() + 1);
		updateWines();
	}
	
	private synchronized double calculateRating(String wine, double rating) {
		double result = 0;
		Wine wineToRate = getWine(wine);
		result = wineToRate.getRating() * wineToRate.getNumberOfReviews() + rating;
		result /= wineToRate.getNumberOfReviews() + 1;
		return result;
	}

	public synchronized boolean wineExists(String wine) {
		return wineList.containsKey(wine);
	}

	public synchronized Wine getWine(String wine) {
		return wineList.get(wine);
	}

	public synchronized Sale getWineSaleBySeller(String wineName, String loggedUser) {
		Wine wine = wineList.get(wineName);
		return wine.getSaleBySeller(loggedUser);
	}

	public synchronized void addSaleToWine(String wineName, Sale sale) throws IOException {
		Wine wine = wineList.get(wineName);
		wine.addSale(sale);
		updateWines();
	}
}
