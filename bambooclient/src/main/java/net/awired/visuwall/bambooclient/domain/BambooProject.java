package net.awired.visuwall.bambooclient.domain;

public class BambooProject {

    private String name;

    private String key;

    private String link;

    private Boolean building;

    private int[] buildNumbers;

    private BambooBuild currentBuild;

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

    public int[] getBuildNumbers() {
        return buildNumbers;
    }

    public void setBuildNumbers(int[] buildNumbers) {
        this.buildNumbers = buildNumbers;
    }

    public BambooBuild getCurrentBuild() {
        return currentBuild;
    }

    public void setCurrentBuild(BambooBuild currentBuild) {
        this.currentBuild = currentBuild;
    }
}
