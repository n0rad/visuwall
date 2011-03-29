package net.awired.visuwall.bambooclient.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "plan")
public class Plan {

    @XmlAttribute
    public Boolean enabled;

    @XmlAttribute
    public String type;

    @XmlAttribute
    public String name;

    @XmlAttribute
    public String key;

    public Link link;
}
