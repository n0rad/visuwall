package net.awired.visuwall.plugin.teamcity;

import static org.junit.Assert.assertTrue;

import java.net.URL;

import net.awired.visuwall.api.domain.SoftwareId;

import org.junit.Test;

public class TeamCityPluginIT {

    @Test
    public void should_be_compatible() throws Exception {
        TeamCityPlugin teamCityPlugin = new TeamCityPlugin();
        SoftwareId softwareId = teamCityPlugin.getSoftwareId(new URL("http://localhost:8111"),
                teamCityPlugin.getPropertiesWithDefaultValue());

        assertTrue(softwareId.isCompatible());

    }

}
