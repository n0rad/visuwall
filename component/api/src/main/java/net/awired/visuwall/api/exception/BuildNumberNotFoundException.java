package net.awired.visuwall.api.exception;

public class BuildNumberNotFoundException extends Exception {

    private static final long serialVersionUID = -1518312632648478201L;

    public BuildNumberNotFoundException(Exception e) {
        super(e);
    }

}
