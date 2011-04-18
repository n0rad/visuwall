package net.awired.visuwall.core.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SoftwareAccess {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String url;
    private String login;
    private String password;
    private Software software;

    // TODO on next version
    // private boolean allProject;
    // private List<ProjectId> projectIds;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SoftwareAccess other = (SoftwareAccess) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
