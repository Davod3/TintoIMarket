package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

import catalogs.UserCatalog;
import catalogs.WineCatalog;
import domain.Sale;
import domain.SaleTransaction;
import utils.FileIntegrityViolationException;
import utils.FileUtils;
import utils.LogUtils;

/**
 * The SellHandler class represents the action of selling a wine. 
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class SellHandler {
	
	private static SellHandler instance = null;
	
	/**
	 * Puts on sale the given quantity of the given wine for the given price.
	 * If the given wine does not exist, send an error message.
	 * 
	 * @param inStream							Stream for receiving input
	 * @param outStream							Stream for outputting result		
	 * @param loggedUser						The user who is selling the wine
	 * @throws ClassNotFoundException			When trying to find the class of an object
	 * 											that does not match/exist
	 * @throws IOException						When inStream does not receive input
	 * 											or the outStream can't send the result message		
	 * @throws NoSuchAlgorithmException 		If the requested algorithm is not available
	 * @throws KeyStoreException 				If an exception occurs while accessing the keystore
	 * @throws SignatureException 				When an error occurs while signing an object
	 * @throws UnrecoverableKeyException 		If the key cannot be recovered
	 * @throws FileIntegrityViolationException 	If the loaded file's is corrupted
	 * @throws InvalidKeyException 				If the key is invalid
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeySpecException 
	 * @throws CertificateException 
	 */
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream, String loggedUser)
			throws ClassNotFoundException, IOException, NoSuchAlgorithmException,
			InvalidKeyException, UnrecoverableKeyException, SignatureException,
			KeyStoreException, FileIntegrityViolationException, CertificateException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		//Get Wine's Catalog only instance
		WineCatalog wineCatalog = WineCatalog.getInstance();
		
		SignedObject signedTransaction = (SignedObject) inStream.readObject();
		
		assert(signedTransaction.verify(UserCatalog.getInstance().getUserCertificate(loggedUser).getPublicKey(), Signature.getInstance("MD5withRSA")));
		
		SaleTransaction st = (SaleTransaction) signedTransaction.getObject();
		
		String wine = st.getWineid();
		double value = st.getUnitValue();
		int quantity = st.getNumUnits();
		
		//Create result message
		String result = "";
		//Check if wine exists
		if (wineCatalog.wineExists(wine)) {
			//Attempt to see if there is already a sale on this wine by this user
			Sale sale = wineCatalog.getWineSaleBySeller(wine, loggedUser);
			//If sale already exists
			if (sale != null) {
				//Add the units to the quantity already there
				sale.setQuantity(sale.getQuantity() + quantity);
				//If the price is not the same
				if (sale.getValue() != value) {
					//Set new price
					sale.setValue(value);
				}
				wineCatalog.updateSaleToWine(wine, sale);
			}
			//If sale does not exist
			else {
				//Create new sale and update Wine's Catalog
				sale = new Sale(loggedUser, value, quantity, wine);
				wineCatalog.addSaleToWine(wine, sale);
			}
			
			LogUtils.getInstance().addTransaction(signedTransaction);
			
			result = "Wine " + wine
					+ " has been successfully put on sale" + FileUtils.EOL;
		}
		else {
			result  = "Wine " + wine
					+ " doesn't exist, try again with another wine" + FileUtils.EOL;
		}
		//Send result message
		outStream.writeObject(result);
	}
	
	/**
	 * Returns the unique instance of the SellHandler class.
	 * If there is no instance of the class, a new one is created and returned.
	 * 
	 * @return	the unique instance of the SellHandler class
	 */
	public static SellHandler getInstance() {
		if (instance == null)
			instance = new SellHandler();
		return instance;
	}
}
