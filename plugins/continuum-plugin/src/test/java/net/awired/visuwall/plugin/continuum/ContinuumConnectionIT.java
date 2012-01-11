package net.awired.visuwall.plugin.continuum;

import static java.util.Arrays.asList;

import java.util.List;

import net.awired.visuwall.api.domain.SoftwareProjectId;

import org.junit.Test;

public class ContinuumConnectionIT {

    @Test
    public void test() throws Exception {

        ContinuumConnection connection = new ContinuumConnection();
        connection.connect("http://vmbuild.apache.org/continuum", "", "");

        List<SoftwareProjectId> softwareProjectIds = connection.findSoftwareProjectIdsByViews(asList("Apache Commons"));
        for (SoftwareProjectId softwareProjectId : softwareProjectIds) {
            System.out.println("----");
            String buildId = connection.getLastBuildId(softwareProjectId);
            connection.getName(softwareProjectId);
            connection.getMavenId(softwareProjectId);
            connection.getBuildCommiters(softwareProjectId, buildId);
            connection.getBuildTime(softwareProjectId, buildId);
            connection.getDescription(softwareProjectId);
            connection.getBuildIds(softwareProjectId);
            connection.getBuildState(softwareProjectId, buildId);
            connection.getEstimatedFinishTime(softwareProjectId, buildId);
            connection.findViews();
        }
    }
}
