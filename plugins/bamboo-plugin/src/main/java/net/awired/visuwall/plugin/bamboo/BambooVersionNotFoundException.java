package net.awired.visuwall.plugin.bamboo;

public class BambooVersionNotFoundException extends Exception {

    private static final long serialVersionUID = -222945705160121465L;

    public BambooVersionNotFoundException(String msg, Exception e) {
        super(msg, e);
    }

}
