package domain;

import java.io.File;
import java.util.List;

public class Wine {
	
	private String name;
	private File image;
	private int rating;
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
}
