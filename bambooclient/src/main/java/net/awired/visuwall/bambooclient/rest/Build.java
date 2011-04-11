package net.awired.visuwall.bambooclient.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "build")
public class Build {

    @XmlAttribute
    public String state;

    @XmlAttribute
    public String key;

}
