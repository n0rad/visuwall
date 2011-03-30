package net.awired.visuwall.api.plugin;


public interface Plugin {

    void setLogin(String login);

    void setPassword(String password);

    void setUrl(String url);

    void init();

}