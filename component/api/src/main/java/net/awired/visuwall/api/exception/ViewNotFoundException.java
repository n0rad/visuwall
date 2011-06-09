package net.awired.visuwall.api.exception;

public class ViewNotFoundException extends Exception {

	private static final long serialVersionUID = 4201239975744058714L;

	public ViewNotFoundException(String msg) {
		super(msg);
    }

	public ViewNotFoundException(String msg, Throwable t) {
		super(msg, t);
	}

}
