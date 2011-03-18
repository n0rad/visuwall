package net.awired.visuwall.api.service;


public interface Service {

    void setLogin(String login);

    void setPassword(String password);

    void setUrl(String url);

    void init();

}