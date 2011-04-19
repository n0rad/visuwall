package net.awired.visuwall.plugin.sonar.exception;

public class SonarMetricsNotFoundException extends Exception {

    private static final long serialVersionUID = -405181214114015401L;

    public SonarMetricsNotFoundException(String cause, Exception e) {
        super(cause, e);
    }

}
