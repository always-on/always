package edu.wpi.always.cm.utils.exceptions;

public class RNotImplementedException extends RuntimeException {

	private static final long serialVersionUID = 8937594357549352858L;

	public RNotImplementedException() {
		super();
	}

	public RNotImplementedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public RNotImplementedException(String arg0) {
		super(arg0);
	}

	public RNotImplementedException(Throwable arg0) {
		super(arg0);
	}

}
