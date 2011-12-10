package net.awired.visuwall.plugin.jenkins;

import java.net.URL;

import org.junit.Test;

public class JenkinsPluginITest {

    @Test
    public void should_manage_jenkins_ci() throws Exception {
        JenkinsPlugin jenkinsPlugin = new JenkinsPlugin();
        jenkinsPlugin.getSoftwareId(new URL("http://ci.jenkins-ci.org"));
    }

    @Test
    public void should_manage_jenkins_jboss() throws Exception {
        JenkinsPlugin jenkinsPlugin = new JenkinsPlugin();
        jenkinsPlugin.getSoftwareId(new URL("http://hudson.jboss.org/jenkins"));
    }
}
