package net.awired.visuwall.plugin.teamcity;

import static org.junit.Assert.assertTrue;

import java.util.List;

import net.awired.visuwall.api.domain.SoftwareProjectId;

import org.junit.Test;

public class TeamCityConnectionIT {

    @Test
    public void test() throws Exception {
        TeamCityConnection connection = new TeamCityConnection();
        connection.connect("http://localhost:8111", "guest", "");

        List<SoftwareProjectId> softwareProjectIds = connection.findSoftwareProjectIdsByViews(connection.findViews());
        for (SoftwareProjectId softwareProjectId : softwareProjectIds) {
            String buildId = connection.getLastBuildId(softwareProjectId);
            boolean building = connection.isBuilding(softwareProjectId, buildId);
            assertTrue(softwareProjectId.getProjectId() + " is not building", building);
        }
    }

}
