package net.awired.clients.bamboo.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Response {

    private String auth;

    @XmlElements({ @XmlElement(name = "build") })
    private List<Build23> builds = new ArrayList<Build23>();

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getAuth() {
        return auth;
    }

    public void setBuilds(List<Build23> builds) {
        this.builds = builds;
    }

    public List<Build23> getBuilds() {
        return builds;
    }

}
