package com.jsmadja.wall.projectwall.domain;

import com.jsmadja.wall.projectwall.service.Service;

public class SoftwareAccess {

    private Software software;

    private String url;
    private String login;
    private String password;

    public SoftwareAccess(Software software, String url) {
        this.software = software;
        this.url = url;
    }

    public SoftwareAccess(Software software, String url, String login, String password) {
        this(software, url);
        this.login = login;
        this.password = password;
    }

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public Service createService() {
        Service service = software.getService();
        service.setUrl(url);
        service.setLogin(login);
        service.setPassword(password);
        service.init();
        return service;
    }

}
