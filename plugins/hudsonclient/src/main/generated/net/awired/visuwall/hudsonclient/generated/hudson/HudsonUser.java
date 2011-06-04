package net.awired.visuwall.hudsonclient.generated.hudson;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

class Property {
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "user")
public class HudsonUser {

    private String id;
    private String name;
    private String email;

    private List<Property> property;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        if (email != null)
            return email;
        if (property != null) {
            for (Property prop : property) {
                String email = prop.getAddress();
                if (email != null) {
                    return email;
                }
            }
        }
        return null;
    }

    public List<Property> getProperty() {
        return property;
    }

    public void setProperty(List<Property> property) {
        this.property = property;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
