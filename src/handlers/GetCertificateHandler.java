package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

import catalogs.UserCatalog;

/**
 * The GetCertificateHandler class allows you to obtain a certificate. 
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class GetCertificateHandler {
	
	private static GetCertificateHandler instance = null;
	
	/**
	 * Returns the unique instance of the GetCertificateHandler class.
	 * If there is no instance of the class, a new one is created and returned.
	 * 
	 * @return	the unique instance of the GetCertificateHandler class
	 */
	public static GetCertificateHandler getInstance() {
		if (instance == null) 
			instance = new GetCertificateHandler();
		return instance;
	}
	
	/**
	 * Returns server's certificate
	 * 
	 * @param inStream								Stream for receiving input
	 * @param outStream								Stream for outputting result
	 * @throws ClassNotFoundException				When trying to find the class of an object
	 * 												that does not match/exist
	 * @throws IOException							When inStream does not receive input
	 * 												or the outStream can't send the result message
	 * @throws InvalidKeyException					If the key is invalid
	 * @throws CertificateException					When an error occurs while generating the certificate
	 * 												from the fileInputStream
	 * @throws NoSuchAlgorithmException				If the requested algorithm is not available
	 * @throws InvalidKeySpecException				If the key is invalid
	 * @throws NoSuchPaddingException				If the padding scheme is not available
	 * @throws InvalidAlgorithmParameterException	If an invalid algorithm parameter is passed to a method
	 */
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream)
			throws ClassNotFoundException, IOException, InvalidKeyException,
			CertificateException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidAlgorithmParameterException {
		String alias = (String) inStream.readObject();
		
		Certificate cer = UserCatalog.getInstance().getUserCertificate(alias);
		
		outStream.writeObject(cer);
	}
}
