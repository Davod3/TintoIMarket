package catalogs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import domain.Sale;
import domain.Wine;
import utils.FileIntegrityViolationException;
import utils.VerifyHash;

/**
 * The WineCatalog class represents the catalog with all wines.
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class WineCatalog {

	private static WineCatalog instance = null;
	private Map<String, Wine> wineList;
	private static final String WINE_FILE_PATH = "server_files/storage/wines.txt";
	private static final String SALES_FILE_PATH = "server_files/storage/sales.txt";
	private static final String SEPARATOR = ":";
	private static final String EOL = System.lineSeparator();

	/**
	 * Creates a WineCatalog and loads all wines from a specific file.
	 * 
	 * @throws IOException	When an I/O error occurs while loading all wines
	 * @throws NoSuchAlgorithmException 
	 * @throws ClassNotFoundException 
	 * @throws FileIntegrityViolationException 
	 */
	private WineCatalog() throws IOException, ClassNotFoundException, NoSuchAlgorithmException, FileIntegrityViolationException {
		wineList = loadWines();
	}

	/**
	 * Returns the unique instance of the WineCatalog class.
	 * If there is no instance of the class, a new one is created and returned.
	 * 
	 * @return					The unique instance of the WineCatalog class
	 * @throws IOException		When an I/O error occurs while reading from a file
	 * @throws NoSuchAlgorithmException 
	 * @throws ClassNotFoundException 
	 * @throws FileIntegrityViolationException 
	 */
	public static WineCatalog getInstance() throws IOException, ClassNotFoundException, NoSuchAlgorithmException, FileIntegrityViolationException {
		if (instance == null)
			instance = new WineCatalog();
		return instance;
	}

	/**
	 * Loads all wines from a specific file.
	 * 
	 * @return					A map with all wines
	 * @throws IOException		When an I/O error occurs
	 * 							while reading/writing to a file
	 * @throws NoSuchAlgorithmException 
	 * @throws ClassNotFoundException 
	 * @throws FileIntegrityViolationException 
	 */
	private synchronized HashMap<String, Wine> loadWines()
			throws IOException, ClassNotFoundException, NoSuchAlgorithmException, FileIntegrityViolationException {
		//Create a map to store all wines
		HashMap<String, Wine> map = new HashMap<String, Wine>();
		//Get file with all wines
		File wineFile = new File(WINE_FILE_PATH);
		wineFile.getParentFile().mkdirs();
		wineFile.createNewFile(); // Make sure file exists before reading
		
		
		//Verify integrity
		VerifyHash.getInstance().verify(wineFile, WINE_FILE_PATH);
		
		//Open reader to read from file
		BufferedReader br = new BufferedReader(new FileReader(WINE_FILE_PATH));
		//Read each line from file
		String line;
		while ((line = br.readLine()) != null) {
			String[] splitData = line.split(SEPARATOR);
			//Create new Wine
			Wine newWine = new Wine(splitData[0], new File(splitData[1]));
			newWine.setRating(Double.parseDouble(splitData[2]));
			newWine.setNumberOfReviews(Integer.parseInt(splitData[3]));
			//Load wine sales
			loadSales(newWine);
			//Insert wine into map of wines
			map.put(splitData[0], newWine);
		}
		br.close();
		return map;
	}

	/**
	 * Loads all sales of a given wine from a specific file.
	 * 
	 * @param wine				The wine for which we want the sales
	 * @throws IOException		When an I/O error occurs while reading/writing to a file
	 * @throws FileIntegrityViolationException 
	 * @throws NoSuchAlgorithmException 
	 * @throws ClassNotFoundException 
	 */
	private synchronized void loadSales(Wine wine) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, FileIntegrityViolationException {
		//Get file with wine sales
		File saleFile = new File(SALES_FILE_PATH);
		saleFile.createNewFile();
		
		//Check integrity
		
		VerifyHash.getInstance().verify(saleFile, SALES_FILE_PATH);
		
		//Open reader to read from file
		BufferedReader br = new BufferedReader(new FileReader(SALES_FILE_PATH));
		//Read each line from file
		String line;
		while ((line = br.readLine()) != null) {
			String[] splitData = line.split(SEPARATOR);
			//If sale matches with wine
			if (splitData[0].equals(wine.getName())) {
				//Create new Sale
				Sale newSale = new Sale(splitData[1], Double.parseDouble(splitData[2]),
						Integer.parseInt(splitData[3]), splitData[0]);
				//Add sale to the list of sales of that wine
				wine.addSale(newSale);
			}
		}
		br.close();
	}

	/**
	 * Updates all sales and wines to wine/sales file.
	 * 
	 * @throws IOException	When an I/O error occurs while reading/writing to a file
	 * @throws NoSuchAlgorithmException 
	 * @throws ClassNotFoundException 
	 * @throws FileIntegrityViolationException 
	 */
	public synchronized void updateWines() throws IOException, NoSuchAlgorithmException, ClassNotFoundException, FileIntegrityViolationException {
		
		//Get all wines
		Set<String> keys = wineList.keySet();
		StringBuilder sbWines = new StringBuilder();
		StringBuilder sbSales = new StringBuilder();

		for (String key : keys) {
			//Get wine
			Wine wine = wineList.get(key);
			//Get wine's information
			sbWines.append(wine.getName() + SEPARATOR + wine.getImageName() + SEPARATOR
					+ wine.getRating() + SEPARATOR + wine.getNumberOfReviews() + EOL);
			//Get wine's sales
			getSales(sbSales, wine);
		}
		//Write all wines
		BufferedWriter bwWines = new BufferedWriter(new FileWriter(WINE_FILE_PATH));
		String winesContent = sbWines.toString();
		bwWines.write(winesContent);
		VerifyHash.getInstance().updateHash(winesContent.getBytes(), WINE_FILE_PATH);
		
		//Write all sales
		BufferedWriter bwSales = new BufferedWriter(new FileWriter(SALES_FILE_PATH));
		String salesContent = sbSales.toString();
		bwSales.write(salesContent);
		VerifyHash.getInstance().updateHash(salesContent.getBytes(), SALES_FILE_PATH);

		//Close resources
		bwWines.close();
		bwSales.close();
	}

	/**
	 * Writes all sales information of a given wine to the given stringBuilder.
	 * 
	 * @param sb		The stringBuilder
	 * @param wine		The wine for which we want all the sales
	 */
	private synchronized void getSales(StringBuilder sb, Wine wine) {
		//Get all sales
		List<Sale> sales = wine.getSales();
		
		for(Sale sale : sales) {
			//Get sale information
			sb.append(sale.getWineName() + SEPARATOR + sale.getSeller()
					+ SEPARATOR + sale.getValue() + SEPARATOR +
					sale.getQuantity() + EOL);
		}
	}

	/**
	 * Adds a new wine to wine catalog.
	 * 
	 * @param wine				The name of the wine we want to add
	 * @param received			The image associated to the wine
	 * @return					True if wine was added successfully,
	 * 							false otherwise
	 * @throws IOException		When an I/O error occurs while
	 * 							reading/writing to a file
	 * @throws NoSuchAlgorithmException 
	 * @throws ClassNotFoundException 
	 * @throws FileIntegrityViolationException 
	 */
	public synchronized boolean createWine(String wine, File received)
			throws IOException, NoSuchAlgorithmException, ClassNotFoundException, FileIntegrityViolationException {
		//Create new Wine
		Wine newWine = new Wine(wine, received);
		//If wine does not exist already in dataBase
		if (!wineList.containsKey(wine)) {
			//Add new wine to WineCatalog
			wineList.put(wine, newWine);
			updateWines();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Rates a wine given his name and rating.
	 * 
	 * @param wine				The name of the wine we want to rate
	 * @param rating			The rating
	 * @throws IOException		When an I/O error occurs while reading/writing to a file
	 * @throws NoSuchAlgorithmException 
	 * @throws ClassNotFoundException 
	 * @throws FileIntegrityViolationException 
	 */
	public synchronized void rate(String wine, double rating)
			throws IOException, NoSuchAlgorithmException, ClassNotFoundException, FileIntegrityViolationException {
		//Get wine
		Wine wineToRate = getWine(wine);
		//Set his rating
		wineToRate.setRating(calculateRating(wine, rating));
		//Set numbers of reviews
		wineToRate.setNumberOfReviews(wineToRate.getNumberOfReviews() + 1);
		updateWines();
	}
	
	/**
	 * Recalculates the rating of a given wine, given a new rating.
	 * 
	 * @param wine			The wine for which we want to calculate the rating
	 * @param rating		The rating for the calculus
	 * @return				The new average rating
	 */
	private synchronized double calculateRating(String wine, double rating) {
		//Create result
		double result = 0;
		//Get wine
		Wine wineToRate = getWine(wine);
		//Calculate rating with number of reviews
		result = wineToRate.getRating() * wineToRate.getNumberOfReviews() + rating;
		result /= wineToRate.getNumberOfReviews() + 1;
		return result;
	}

	/**
	 * Checks if a given wine exists in dataBase.
	 * 
	 * @param wine		The wine for which we want to check
	 * @return			True if the given wine already exists in dataBase,
	 * 					false otherwise
	 */
	public synchronized boolean wineExists(String wine) {
		return wineList.containsKey(wine);
	}

	/**
	 * Returns the wine associated to the given wine name.
	 * 
	 * @param wine		The wine we want to look for
	 * @return			The respective wine
	 */
	public synchronized Wine getWine(String wine) {
		return wineList.get(wine);
	}

	/**
	 * Returns a wine's sale associated to the given seller.
	 * 
	 * @param wine			The name of the wine
	 * @param loggedUser	The current user/seller
	 * @return				The wine's sale of that seller
	 */
	public synchronized Sale getWineSaleBySeller(String wineName, String loggedUser) {
		Wine wine = wineList.get(wineName);
		return wine.getSaleBySeller(loggedUser);
	}

	/**
	 * Adds a given sale to the given wine.
	 * 
	 * @param wineName			The name of the wine
	 * @param sale				The sale we want to add to the sale list of the wine
	 * @throws IOException		When an I/O error occurs while reading/writing to a file
	 * @throws NoSuchAlgorithmException 
	 * @throws ClassNotFoundException 
	 * @throws FileIntegrityViolationException 
	 */
	public synchronized void addSaleToWine(String wineName, Sale sale)
			throws IOException, NoSuchAlgorithmException, ClassNotFoundException, FileIntegrityViolationException {
		Wine wine = wineList.get(wineName);
		wine.addSale(sale);
		updateWines();
	}

	/**
	 * Removes a wine's sale from the given seller.
	 * 
	 * @param wine				The wine for which we want to get the sale
	 * @param seller			The user for which we want to delete the sale
	 * @throws IOException		When an I/O error occurs while reading/writing to a file
	 * @throws NoSuchAlgorithmException 
	 * @throws ClassNotFoundException 
	 * @throws FileIntegrityViolationException 
	 */
	public synchronized void removeSaleFromSeller(String wine, String seller)
			throws IOException, NoSuchAlgorithmException, ClassNotFoundException, FileIntegrityViolationException {
		wineList.get(wine).getSales().remove(getWineSaleBySeller(wine, seller));
		updateWines();
	}
}
