package net.awired.clients.pivotaltracker.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "stories")
@XmlAccessorType(XmlAccessType.FIELD)
public class Stories {

    @XmlElements({ @XmlElement(name = "story") })
    private List<Story> stories = new ArrayList<Story>();

    public Story get(int i) {
        return stories.get(i);
    }

    public void add(Story story) {
        this.stories.add(story);
    }

    public Collection<Story> all() {
        return Collections.unmodifiableCollection(stories);
    }

}
