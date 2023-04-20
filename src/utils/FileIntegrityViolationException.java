package utils;

public class FileIntegrityViolationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public FileIntegrityViolationException(String errorMessage) {
		super(errorMessage);
	}
}
