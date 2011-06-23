package net.awired.visuwall.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.plugin.bamboo.BambooConnection;
import net.awired.visuwall.plugin.hudson.HudsonConnection;
import net.awired.visuwall.plugin.jenkins.JenkinsConnection;
import net.awired.visuwall.plugin.sonar.SonarConnection;
import net.awired.visuwall.plugin.teamcity.TeamCityConnection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BasicCapabilityParametersTest {

    private BasicCapability connection;

    public BasicCapabilityParametersTest(Class<BasicCapability> clazz) throws InstantiationException, IllegalAccessException {
        this.connection = clazz.newInstance();
    }

    @Parameters
    public static Collection<Object[]> createData() {
        @SuppressWarnings("unchecked")
        List<Class<? extends BasicCapability>> connections = Arrays.asList(//
                JenkinsConnection.class, //
                HudsonConnection.class, //
                TeamCityConnection.class, //
                BambooConnection.class, //
                SonarConnection.class);
        List<Object[]> objects = new ArrayList<Object[]>();
        for (Class<? extends BasicCapability> clazz : connections) {
            objects.add(new Object[] { clazz });
        }
        return objects;
    }

    @Test(expected = NullPointerException.class)
    public void cant_pass_null_as_url_param() throws ConnectionException {
        connection.connect(null, null, null);
    }

    @Test
    public void can_pass_null_as_login_param() throws ConnectionException {
        connection.connect("url", null, null);
    }

    @Test
    public void can_pass_null_as_password_param() throws ConnectionException {
        connection.connect("url", "login", null);
    }

    @Test
    public void should_close_non_connected_connection() {
        connection.close();
    }

    @Test(expected = IllegalStateException.class)
    public void cant_call_findAllSoftwareProjectIds_when_disconnected() {
        connection.findAllSoftwareProjectIds();
    }

    @Test(expected = NullPointerException.class)
    public void cant_call_find_getDescription_with_null_param() throws ProjectNotFoundException {
        connection.getDescription(null);
    }

    @Test(expected = NullPointerException.class)
    public void cant_call_findSoftwareProjectIdsByNames_with_null_param() {
        connection.findSoftwareProjectIdsByNames(null);
    }

}
