package net.awired.clients.deployit.resource;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "repository-object-ids")
@XmlAccessorType(XmlAccessType.FIELD)
public class RepositoryObjectIds {

    @XmlElements({ @XmlElement(name = "repository-object-id") })
    private List<String> repositoryObjectIds;

    public void setRepositoryObjectIds(List<String> repositoryObjectIds) {
        this.repositoryObjectIds = repositoryObjectIds;
    }

    public List<String> getRepositoryObjectIds() {
        return repositoryObjectIds;
    }

}
