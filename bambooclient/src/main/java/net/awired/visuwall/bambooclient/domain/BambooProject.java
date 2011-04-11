package net.awired.visuwall.bambooclient.domain;

public class BambooProject {

    private String name;

    private String key;

    private String link;

    private Boolean building;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean isBuilding() {
        return building;
    }

    public void setBuilding(Boolean building) {
        this.building = building;
    }
}
