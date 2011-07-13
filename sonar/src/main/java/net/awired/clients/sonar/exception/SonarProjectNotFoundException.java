package net.awired.clients.sonar.exception;

public class SonarProjectNotFoundException extends Exception {

    private static final long serialVersionUID = -5558821255643291117L;

    public SonarProjectNotFoundException(String msg) {
        super(msg);
    }

    public SonarProjectNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
