package net.awired.visuwall.server.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import net.awired.visuwall.api.plugin.BuildPlugin;
import net.awired.visuwall.api.plugin.QualityPlugin;


@Entity
public class SoftwareAccess {

    @Id
    @GeneratedValue
    private Long id;
    
    private String name;

    @OneToOne(fetch=FetchType.EAGER)
    private Software software;

    private String url;
    private String login;
    private String password;

    public SoftwareAccess() {

    }

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

    public BuildPlugin createBuildService() {
        BuildPlugin service = software.getBuildService();
        service.setUrl(url);
        service.setLogin(login);
        service.setPassword(password);
        service.init();
        return service;
    }

    public QualityPlugin createQualityService() {
        QualityPlugin service = software.getQualityService();
        service.setUrl(url);
        service.setLogin(login);
        service.setPassword(password);
        service.init();
        return service;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SoftwareAccess)) {
            return false;
        }
        SoftwareAccess sa = (SoftwareAccess) obj;
        return id.equals(sa.id);
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
