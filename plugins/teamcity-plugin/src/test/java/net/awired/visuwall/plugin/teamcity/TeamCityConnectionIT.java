package net.awired.visuwall.plugin.teamcity;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

import net.awired.visuwall.api.domain.BuildState;
import net.awired.visuwall.api.domain.SoftwareProjectId;

import org.junit.Test;

public class TeamCityConnectionIT {

    @Test
    public void test() throws Exception {
        TeamCityConnection connection = new TeamCityConnection();
        connection.connect("http://localhost:8111", "guest", "");
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("bt15");
        String lastBuildId = connection.getLastBuildId(softwareProjectId);
        System.out.println("Last build id:" + lastBuildId);
        boolean building = connection.isBuilding(softwareProjectId, lastBuildId);
        System.out.println("building:" + building);
        BuildState buildState = connection.getBuildState(softwareProjectId, lastBuildId);
        System.out.println("State:" + buildState);
        Date estimatedFinishTime = connection.getEstimatedFinishTime(softwareProjectId, lastBuildId);
        assertNotNull(estimatedFinishTime);
        System.out.println(estimatedFinishTime);
    }

}
