package net.awired.visuwall.plugin.teamcity.tck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.api.plugin.tck.BuildCapabilityTCK;
import net.awired.visuwall.plugin.teamcity.TeamCityConnection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TeamBuildCapabilityIT implements BuildCapabilityTCK {

    BuildCapability teamcity = new TeamCityConnection();

    @Before
    public void init() throws ConnectionException {
        teamcity.connect(IntegrationTestData.TEAMCITY_URL, "guest", "");
    }

    @Override
    @Test
    public void should_get_build_numbers() throws Exception {
        SoftwareProjectId projectId = amazonProjectSoftwareId();
        List<Integer> buildNumbers = teamcity.getBuildNumbers(projectId);

        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), buildNumbers);
    }

    @Override
    @Test
    @Ignore
    public void should_get_estimated_date() throws Exception {
        SoftwareProjectId projectId = amazonProjectSoftwareId();
        Date estimatedFinishTime = teamcity.getEstimatedFinishTime(projectId, 1);

        assertNotNull(estimatedFinishTime);
    }

    @Override
    @Test
    public void should_get_last_build_number() throws Exception {
        SoftwareProjectId projectId = amazonProjectSoftwareId();
        int lastBuildNumber = teamcity.getLastBuildNumber(projectId);

        assertEquals(6, lastBuildNumber);
    }

    @Override
    @Test
    public void should_get_build_state() throws Exception {
        SoftwareProjectId projectId = amazonProjectSoftwareId();
        State state = teamcity.getBuildState(projectId, 6);

        assertEquals(State.SUCCESS, state);
    }

    @Override
    @Test
    public void should_get_is_building() throws Exception {
        SoftwareProjectId projectId = amazonProjectSoftwareId();
        boolean isBuilding = teamcity.isBuilding(projectId, 1);

        assertFalse(isBuilding);
    }

    private SoftwareProjectId amazonProjectSoftwareId() {
        return new SoftwareProjectId("project54");
    }

}
