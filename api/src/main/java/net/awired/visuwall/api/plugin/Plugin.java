package net.awired.visuwall.api.plugin;

import java.net.URL;


public interface Plugin {
	
//	
//	String getName();
//	
//	int getVersion();
//	
//	boolean isManagable(URL url);
	
    void setLogin(String login);

    void setPassword(String password);

    void setUrl(String url);

    void init();

}