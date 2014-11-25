package net.smalinuxer.frame;

public class ReactorShutDownException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReactorShutDownException() {
		super("Reactor have already shutdown !");
	}

	public ReactorShutDownException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ReactorShutDownException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReactorShutDownException(String message) {
		super(message);
	}

	public ReactorShutDownException(Throwable cause) {
		super(cause);
	}
	
}
