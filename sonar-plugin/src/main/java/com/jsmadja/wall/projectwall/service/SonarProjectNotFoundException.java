package com.jsmadja.wall.projectwall.service;

public class SonarProjectNotFoundException extends Exception {

    private static final long serialVersionUID = -2042779915658611561L;

    public SonarProjectNotFoundException(String cause) {
        super(cause);
    }

}
