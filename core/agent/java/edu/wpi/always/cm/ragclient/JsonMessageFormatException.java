package edu.wpi.always.cm.ragclient;

public class JsonMessageFormatException extends RuntimeException {

	private static final long serialVersionUID = 2041517149475925778L;

	public JsonMessageFormatException () {
		super();
	}

	public JsonMessageFormatException (String message, Throwable cause) {
		super(message, cause);
	}

	public JsonMessageFormatException (String message) {
		super(message);
	}

	public JsonMessageFormatException (Throwable cause) {
		super(cause);
	}

	
	
}
