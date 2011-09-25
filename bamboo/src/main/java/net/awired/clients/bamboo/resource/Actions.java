package net.awired.clients.bamboo.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "actions")
@XmlAccessorType(XmlAccessType.FIELD)
public class Actions {

    @XmlAttribute(name = "max-result")
    private Integer maxResult;

    @XmlAttribute(name = "start-index")
    private Integer startIndex;

    @XmlAttribute(name = "size")
    private Integer size;

    public Integer getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(Integer maxResult) {
        this.maxResult = maxResult;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

}
