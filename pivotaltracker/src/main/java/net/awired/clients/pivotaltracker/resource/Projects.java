package net.awired.clients.pivotaltracker.resource;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "projects")
@XmlAccessorType(XmlAccessType.FIELD)
public class Projects implements Iterable<Project> {

    @XmlElements({ @XmlElement(name = "project") })
    private List<Project> projects;

    public List<Project> getProjects() {
        return projects;
    }

    @Override
    public Iterator<Project> iterator() {
        return projects.iterator();
    }
}
