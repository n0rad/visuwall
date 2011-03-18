package net.awired.visuwall.hudsonclient;

public final class HudsonBuildNotFoundException extends Exception {

    private static final long serialVersionUID = -6609238015272249116L;

    public HudsonBuildNotFoundException(String cause, Throwable t) {
        super(cause, t);
    }

    public HudsonBuildNotFoundException(String cause) {
        super(cause);
    }

}
