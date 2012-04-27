package net.awired.visuwall.plugin.bamboo;

import java.util.Map;

import net.awired.visuwall.api.domain.SoftwareProjectId;

import org.junit.Test;

public class Bamboo23ConnectionIT {

    @Test
    public void test() throws Exception {
        Bamboo23Connection connection = new Bamboo23Connection();
        connection.connect("http://localhost:8085", "admin", "password");
        Map<SoftwareProjectId, String> softwareProjectIds = connection.listSoftwareProjectIds();
        for (SoftwareProjectId softwareProjectId : softwareProjectIds.keySet()) {
            System.out.println(softwareProjectId.getProjectId());
            String name = connection.getName(softwareProjectId);
            System.out.println("name:" + name);
            System.out.println(connection.getDescription(softwareProjectId));
            String lastBuildId = connection.getLastBuildId(softwareProjectId);
            System.out.println("last build id:" + lastBuildId);
            System.out.println("state:" + connection.getBuildState(softwareProjectId, lastBuildId));
            System.out.println("buildTime:" + connection.getBuildTime(softwareProjectId, lastBuildId));
        }
    }

}
