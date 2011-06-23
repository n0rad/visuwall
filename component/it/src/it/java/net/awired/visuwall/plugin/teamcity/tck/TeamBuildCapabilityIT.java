package net.awired.visuwall.plugin.teamcity.tck;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.SoftwareProjectId;
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
        Integer[] buildNumbers = teamcity.getBuildNumbers(projectId);
        System.err.println(Arrays.toString(buildNumbers));
        assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6 }, buildNumbers);
    }

    @Override
    @Test
    @Ignore
    public void should_get_estimated_date() throws Exception {
    }

    @Override
    @Test
    @Ignore
    public void should_get_last_build_number() throws Exception {
    }

    @Override
    @Test
    @Ignore
    public void should_get_last_build_state() throws Exception {
    }

    @Override
    @Test
    @Ignore
    public void should_get_is_building() throws Exception {
    }

    private SoftwareProjectId amazonProjectSoftwareId() {
        return new SoftwareProjectId("project54");
    }

}
