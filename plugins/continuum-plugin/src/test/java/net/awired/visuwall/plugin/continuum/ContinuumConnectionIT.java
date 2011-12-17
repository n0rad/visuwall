package net.awired.visuwall.plugin.continuum;

import java.util.Map;

import net.awired.visuwall.api.domain.SoftwareProjectId;

import org.junit.Test;

public class ContinuumConnectionIT {

    @Test
    public void test() throws Exception {

        ContinuumConnection connection = new ContinuumConnection();
        connection.connect("http://vmbuild.apache.org/continuum", "", "");

        Map<SoftwareProjectId, String> softwareProjectIds = connection.listSoftwareProjectIds();
        for (SoftwareProjectId softwareProjectId : softwareProjectIds.keySet()) {
            System.out.println("----");
            String buildId = connection.getLastBuildId(softwareProjectId);
            System.out.println(connection.getName(softwareProjectId));
            System.out.println(connection.getMavenId(softwareProjectId));
            System.out.println(connection.getBuildCommiters(softwareProjectId, buildId));
            System.out.println(connection.getBuildTime(softwareProjectId, buildId));
            System.out.println(connection.getDescription(softwareProjectId));
            System.out.println(connection.getBuildIds(softwareProjectId));
            System.out.println(connection.getBuildState(softwareProjectId, buildId));
            System.out.println(connection.getEstimatedFinishTime(softwareProjectId, buildId));
            System.out.println(connection.findViews());
        }
    }
}
