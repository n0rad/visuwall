package net.awired.visuwall.bambooclient.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "plans")
public class Plans {

    public Plans plans;

    public List<Plan> plan;
}
