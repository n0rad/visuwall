package net.awired.visuwall.plugin.pivotaltracker;

import java.util.Map;

import net.awired.visuwall.api.domain.SoftwareProjectId;

import org.junit.Test;

public class PivotalTrackerConnectionIT {

    @Test
    public void test() throws Exception {
        PivotalTrackerConnection connection = new PivotalTrackerConnection();
        connection.connect("https://www.pivotaltracker.com", "jsmadja@financeactive.com", "xedy4bsa");

        Map<SoftwareProjectId, String> listSoftwareProjectIds = connection.listSoftwareProjectIds();
        for (SoftwareProjectId softwareProjectId : listSoftwareProjectIds.keySet()) {
            // System.err.println(softwareProjectId.getProjectId());
            // System.err.println(connection.getName(softwareProjectId));
            // System.err.println(connection.getBuildState(softwareProjectId, ""));
            System.err.println(connection.getBuildTime(softwareProjectId, ""));
        }

    }

}
