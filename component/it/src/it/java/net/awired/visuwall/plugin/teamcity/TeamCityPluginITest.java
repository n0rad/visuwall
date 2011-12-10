package net.awired.visuwall.plugin.teamcity;

import static net.awired.visuwall.Urls.LOCAL_TEAMCITY;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;

import org.junit.Test;

public class TeamCityPluginITest {

    // @Test
    // public void should_recognize_teamcity_with_valid_url() throws Exception {
    // TeamCityPlugin plugin = new TeamCityPlugin();
    // SoftwareId softwareId = plugin.getSoftwareId(new URL(LOCAL_TEAMCITY));
    // assertEquals("6.5", softwareId.getVersion());
    // assertEquals("TeamCity", softwareId.getName());
    // assertTrue(softwareId.getWarnings().isEmpty());
    // }

    @Test
    public void test() throws Exception {
        TeamCityPlugin plugin = new TeamCityPlugin();

        Map<String, String> properties = plugin.getPropertiesWithDefaultValue();
        properties.put("login", "jsmadja");
        properties.put("password", "password");

        TeamCityConnection connection = plugin.getConnection(new URL(LOCAL_TEAMCITY), properties);

        Map<SoftwareProjectId, String> softwareProjectIds = connection.listSoftwareProjectIds();
        SoftwareProjectId softwareProjectId = softwareProjectIds.keySet().iterator().next();

        List<String> buildIds = connection.getBuildIds(softwareProjectId);
        System.out.println(buildIds.size() + " ids : " + Arrays.toString(buildIds.toArray()));
        State state = connection.getBuildState(softwareProjectId, buildIds.get(0));
        System.out.println(state);
        String mavenId = connection.getMavenId(softwareProjectId);
        System.out.println("Maven id : " + mavenId);
    }

}
