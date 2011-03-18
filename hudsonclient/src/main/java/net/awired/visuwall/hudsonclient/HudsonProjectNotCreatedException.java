package net.awired.visuwall.hudsonclient;

public final class HudsonProjectNotCreatedException extends Exception {

    private static final long serialVersionUID = -6952748181282352092L;

    public HudsonProjectNotCreatedException(Throwable cause) {
        super(cause);
    }

    public HudsonProjectNotCreatedException(String message, Throwable cause) {
        super(message, cause);
    }

}
