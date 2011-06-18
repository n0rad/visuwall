package net.awired.visuwall.api.exception;


public class ConnectionException extends Exception {

    private static final long serialVersionUID = 2108061876366016121L;

    public ConnectionException(String message, Throwable t) {
        super(message, t);
    }

}
