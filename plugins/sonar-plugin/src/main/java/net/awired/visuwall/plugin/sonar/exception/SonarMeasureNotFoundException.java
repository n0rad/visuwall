package net.awired.visuwall.plugin.sonar.exception;


public class SonarMeasureNotFoundException extends Exception {

	private static final long serialVersionUID = -7205356356110212352L;

	public SonarMeasureNotFoundException(String msg) {
		super(msg);
	}

	public SonarMeasureNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
