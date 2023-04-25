package utils;

/**
 * The FileIntegrityViolationException class represents
 * the exception for a file that is corrupted. 
 * 
 * @author André Dias 		nº 55314
 * @author David Pereira 	nº 56361
 * @author Miguel Cut		nº 56339
 */
public class FileIntegrityViolationException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * This constructor represents the exception
	 * for a file that is corrupted
	 * 
	 * @param errorMessage	The error message to present when the exception is thrown
	 */
	public FileIntegrityViolationException(String errorMessage) {
		super(errorMessage);
	}
}
