package net.awired.visuwall.plugin.teamcity;

import static org.junit.Assert.assertTrue;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import org.junit.Ignore;
import org.junit.Test;

public class TeamCityConnectionITest {

    @Ignore
    @Test
    public void should_get_is_building() throws Exception {
        TeamCityConnection connection = new TeamCityConnection();
        connection.connect("http://localhost:8111");

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("project7");
        String lastBuildNumber = connection.getLastBuildId(softwareProjectId);
        System.out.println(lastBuildNumber);
        boolean isBuilding = connection.isBuilding(softwareProjectId, lastBuildNumber);

        assertTrue(isBuilding);
    }

}
