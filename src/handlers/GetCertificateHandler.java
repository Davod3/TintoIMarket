package handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

import catalogs.UserCatalog;

public class GetCertificateHandler {
	
	private static GetCertificateHandler instance = null;
	
	public static GetCertificateHandler getInstance() {
		if (instance == null) 
			instance = new GetCertificateHandler();
		return instance;
	}
	
	public void run(ObjectInputStream inStream, ObjectOutputStream outStream) throws ClassNotFoundException, IOException, KeyStoreException, InvalidKeyException, CertificateException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		
		String alias = (String) inStream.readObject();
		
		Certificate cer = UserCatalog.getInstance().getUserCertificate(alias);
		
		outStream.writeObject(cer);
		
	}

}
