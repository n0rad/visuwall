package net.awired.visuwall.plugin.sonar.exception;

public class SonarResourceNotFoundException extends Exception {

    private static final long serialVersionUID = 633618427408818570L;

    public SonarResourceNotFoundException(String msg) {
        super(msg);
    }

    public SonarResourceNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
