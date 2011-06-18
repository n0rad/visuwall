package net.awired.visuwall.hudsonclient.exception;

public class DocumentNotLoadedException extends Exception {

    private static final long serialVersionUID = -2052810061328639260L;

    public DocumentNotLoadedException(String msg, Exception e) {
        super(msg, e);
    }

}
