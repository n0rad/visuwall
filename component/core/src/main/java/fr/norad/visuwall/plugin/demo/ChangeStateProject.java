package fr.norad.visuwall.plugin.demo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import fr.norad.visuwall.api.domain.BuildState;
import fr.norad.visuwall.api.domain.BuildTime;
import fr.norad.visuwall.api.domain.Commiter;
import fr.norad.visuwall.api.domain.SoftwareProjectId;
import org.joda.time.DateTime;

public class ChangeStateProject {

    public class Build {
        BuildState buildState;
        String buildId;
        List<Commiter> commiters;
        BuildTime buildTime;
        private boolean building = true;
    }

    private List<Build> builds = new ArrayList<Build>();

    private final SoftwareProjectId projectId;

    public ChangeStateProject() {
        this(null);
    }

    public ChangeStateProject(SoftwareProjectId projectId) {
        this.projectId = projectId;

        Build build = generateNewBuild();
        updateBuildAfterBuilding(build);
        generateNewBuild();
    }

    ////////////////

    boolean isBuilding(boolean notBuildingTime) {
        Build lastBuild = getlastBuild();
        if (notBuildingTime) {
            if (lastBuild.building) {
                updateBuildAfterBuilding(lastBuild);
            }
        } else if (!lastBuild.building) {
            Build newBuild = generateNewBuild();
            return newBuild.building;
        }
        return lastBuild.building;
    }

    private void updateBuildAfterBuilding(Build build) {

        //state
        int stateIndex = (int) (Math.random() * (((BuildState.values().length - 1) - 0) + 1));
        build.buildState = BuildState.values()[stateIndex];

        build.buildTime.setDuration((new Date().getTime() - build.buildTime.getStartTime().getTime()));
        build.building = false;
    }

    private Build generateNewBuild() {
        String lastBuildId = "0";
        if (builds.size() > 0) {
            lastBuildId = getlastBuild().buildId;
        }

        Build build = new Build();
        build.buildId = String.valueOf(Integer.valueOf(lastBuildId) + 1);

        // commiters
        build.commiters = new ArrayList<Commiter>();
        if (new Random().nextBoolean()) {
            Commiter alemaire = new Commiter("alemaire");
            alemaire.setEmail("alemaire@xebia.fr");
            build.commiters.add(alemaire);
        }
        if (new Random().nextBoolean()) {
            Commiter jsmadja = new Commiter("jsmadja");
            jsmadja.setEmail("jsmadja@xebia.fr");
            build.commiters.add(jsmadja);
        }

        // BuildTime
        build.buildTime = new BuildTime();
        build.buildTime.setStartTime(new Date());
        build.building = true;

        builds.add(build);
        return build;
    }

    private Build getBuildFromId(String buildId) {
        for (Build build : builds) {
            if (buildId.equals(build.buildId)) {
                return build;
            }
        }
        return null;
    }

    private Build getlastBuild() {
        return builds.get(builds.size() - 1);
    }

    //////////////////////

    public boolean isBuilding() {
        return isBuilding(Calendar.getInstance().get(Calendar.SECOND) < 30);
    }

    // TODO should be in build 
    public Date estimatedFinishTime() {
        return new DateTime(getlastBuild().buildTime.getStartTime()).plusMinutes(1).toDate();
    }

    public SoftwareProjectId getProjectId() {
        return projectId;
    }

    public String getLastBuildId() {
        return getlastBuild().buildId;
    }

    public List<Commiter> getCommiters(String buildId) {
        return getBuildFromId(buildId).commiters;
    }

    public BuildTime getBuildTime(String buildId) {
        return getBuildFromId(buildId).buildTime;
    }

    public List<String> getbuildIds() {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Build build : builds) {
            arrayList.add(build.buildId);
        }
        return arrayList;
    }

    public BuildState getBuildState(String buildId) {
        return getBuildFromId(buildId).buildState;
    }

}
