package edu.wpi.always.cm.ragclient;

public class InvalidMessageTypeException extends RuntimeException {

	private static final long serialVersionUID = 3719582622549333413L;

	public InvalidMessageTypeException () {
		super();
	}

	public InvalidMessageTypeException (String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public InvalidMessageTypeException (String arg0) {
		super(arg0);
	}

	public InvalidMessageTypeException (Throwable arg0) {
		super(arg0);
	}

}
