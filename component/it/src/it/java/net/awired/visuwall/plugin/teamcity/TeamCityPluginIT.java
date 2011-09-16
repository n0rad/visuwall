package net.awired.visuwall.plugin.teamcity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.net.URL;
import net.awired.visuwall.Urls;
import net.awired.visuwall.api.domain.SoftwareId;
import org.junit.Test;

public class TeamCityPluginIT {

    @Test
    public void should_recognize_teamcity_with_valid_url() throws Exception {
        TeamCityPlugin plugin = new TeamCityPlugin();
        SoftwareId softwareId = plugin.getSoftwareId(new URL(Urls.LOCAL_TEAMCITY));
        assertEquals("6.5", softwareId.getVersion());
        assertEquals("TeamCity", softwareId.getName());
        assertTrue(softwareId.getWarnings().isEmpty());
    }

}
