package com.jsmadja.wall.projectwall.exception;


public final class BuildNotFoundException extends Exception {

    private static final long serialVersionUID = 615519485544622495L;

    public BuildNotFoundException(String cause) {
        super(cause);
    }

    public BuildNotFoundException(Throwable t) {
        super(t);
    }

}
