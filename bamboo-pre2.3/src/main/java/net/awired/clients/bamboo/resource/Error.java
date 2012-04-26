package net.awired.clients.bamboo.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "errors")
@XmlAccessorType(XmlAccessType.FIELD)
public class Error {

    @XmlElements({ @XmlElement(name = "error") })
    private List<String> errors = new ArrayList<String>();

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

}
