package Catalogs;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	
	public void sellWine(String wine, int quantity) {
		Wine wineToSell = getWine(wine);
		wineToSell.setWineStock(wineToSell.getWineStock()-quantity);
	}
	
	public Wine getWine(String wine) {
		return wineList.get(wine);
	}

	
}
