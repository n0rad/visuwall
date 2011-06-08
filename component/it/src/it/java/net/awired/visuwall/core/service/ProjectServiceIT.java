package net.awired.visuwall.core.service;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.ConnectionPlugin;
import net.awired.visuwall.plugin.jenkins.JenkinsPlugin;

import org.junit.Test;

public class ProjectServiceIT {

    @Test
    public void github_issue_35() throws BuildNotFoundException, ProjectNotFoundException {
        String jenkinsUrl = "http://ci.awired.net/jenkins/";
        JenkinsPlugin jenkinsPlugin = new JenkinsPlugin();
        ConnectionPlugin connection =  jenkinsPlugin.getConnection(jenkinsUrl, null);
        int buildNumber = 29;
        ProjectId projectId = new ProjectId("Acml");
        projectId.setArtifactId("net.awired.aclm:aclm");
        projectId.addId("JENKINS_ID", "Acml");
        connection.findBuildByBuildNumber(projectId, buildNumber);
    }

}
