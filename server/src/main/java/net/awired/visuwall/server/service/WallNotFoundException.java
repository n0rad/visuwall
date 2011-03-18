package net.awired.visuwall.server.service;

public class WallNotFoundException extends Exception {

    private static final long serialVersionUID = -8160584319934461979L;

    public WallNotFoundException(String cause) {
        super(cause);
    }

}
