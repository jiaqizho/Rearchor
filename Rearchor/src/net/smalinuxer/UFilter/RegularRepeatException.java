package net.smalinuxer.UFilter;


public class RegularRepeatException extends Exception{
	
	/**s
	 * 
	 */
	private static final long serialVersionUID = 2L;

	public RegularRepeatException() {
		super("The regular has set the repeat !");
	}

	public RegularRepeatException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RegularRepeatException(String message, Throwable cause) {
		super(message, cause);
	}

	public RegularRepeatException(String message) {
		super(message);
	}

	public RegularRepeatException(Throwable cause) {
		super(cause);
	}
	
}
