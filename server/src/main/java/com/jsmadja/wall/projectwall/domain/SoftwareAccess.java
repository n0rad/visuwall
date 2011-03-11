package com.jsmadja.wall.projectwall.domain;

import com.jsmadja.wall.projectwall.service.BuildService;
import com.jsmadja.wall.projectwall.service.QualityService;

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

    public BuildService createBuildService() {
        BuildService service = software.getBuildService();
        service.setUrl(url);
        service.setLogin(login);
        service.setPassword(password);
        service.init();
        return service;
    }

    public QualityService createQualityService() {
        QualityService service = software.getQualityService();
        service.setUrl(url);
        service.setLogin(login);
        service.setPassword(password);
        service.init();
        return service;
    }

}
