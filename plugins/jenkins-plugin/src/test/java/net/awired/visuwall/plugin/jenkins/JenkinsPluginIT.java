package net.awired.visuwall.plugin.jenkins;

import java.util.Set;

import net.awired.visuwall.api.domain.SoftwareProjectId;

import org.junit.Test;

public class JenkinsPluginIT {

    @Test
    public void test() throws Exception {
        JenkinsConnection connection = new JenkinsConnection();
        connection.connect("http://10.2.40.63:8989/jenkins", "", "");

        Set<SoftwareProjectId> softwareProjectIds = connection.listSoftwareProjectIds().keySet();
        System.out.println(softwareProjectIds.size());
        for (SoftwareProjectId softwareProjectId : softwareProjectIds) {
            String mavenId = connection.getMavenId(softwareProjectId);
            System.out.println(mavenId);
        }
    }

}
