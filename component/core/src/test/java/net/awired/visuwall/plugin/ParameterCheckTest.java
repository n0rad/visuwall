package net.awired.visuwall.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.plugin.Connection;
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
public class ParameterCheckTest {

    private Connection connection;

    public ParameterCheckTest(Class<Connection> clazz) throws InstantiationException, IllegalAccessException {
        this.connection = clazz.newInstance();
    }

    @Parameters
    public static Collection<Object[]> createData() {
        @SuppressWarnings("unchecked")
        List<Class<? extends Connection>> connections = Arrays.asList(//
                JenkinsConnection.class, //
                HudsonConnection.class, //
                TeamCityConnection.class, //
                BambooConnection.class, //
                SonarConnection.class);
        List<Object[]> objects = new ArrayList<Object[]>();
        for (Class<? extends Connection> clazz : connections) {
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
    public void cant_call_find_all_software_project_ids_when_disconnected() {
        System.err.println(connection);
        connection.findAllSoftwareProjectIds();
    }
}
