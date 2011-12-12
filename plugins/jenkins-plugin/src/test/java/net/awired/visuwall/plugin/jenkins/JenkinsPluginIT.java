package net.awired.visuwall.plugin.jenkins;

import java.net.URL;

import org.junit.Test;

public class JenkinsPluginIT {

    @Test
    public void should_manage_jenkins_ci() throws Exception {
        JenkinsPlugin jenkinsPlugin = new JenkinsPlugin();
        jenkinsPlugin.getSoftwareId(new URL("http://ci.jenkins-ci.org"));
    }

}
