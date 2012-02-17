package net.awired.visuwall.plugin.deployit;

import static org.junit.Assert.assertNotNull;

import java.net.URL;

import net.awired.visuwall.api.domain.SoftwareId;

import org.junit.Test;

public class DeployItPluginIT {

    @Test
    public void should_get_software_id() throws Exception {
        DeployItPlugin plugin = new DeployItPlugin();
        SoftwareId softwareId = plugin.getSoftwareId(new URL("http://localhost:4516"));
        assertNotNull(softwareId);
    }

}
