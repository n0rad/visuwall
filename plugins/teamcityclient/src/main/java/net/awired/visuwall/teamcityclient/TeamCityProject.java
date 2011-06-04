package net.awired.visuwall.teamcityclient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamCityProject {

    @XmlAttribute
    String name;

    @XmlAttribute
    public String id;

    @XmlAttribute
    public String href;

    @XmlAttribute
    public String webUrl;

    @XmlAttribute
    public String description;

    @XmlAttribute
    public boolean archived;

}
