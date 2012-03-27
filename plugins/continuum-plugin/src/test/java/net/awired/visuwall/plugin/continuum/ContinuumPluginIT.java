package net.awired.visuwall.plugin.continuum;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import net.awired.visuwall.api.domain.SoftwareId;

import org.junit.Test;

public class ContinuumPluginIT {

    @Test
    public void test() throws Exception {
        ContinuumPlugin plugin = new ContinuumPlugin();
        URL url = new URL("http://vmbuild.apache.org/continuum");
        SoftwareId softwareId = plugin.getSoftwareId(url, null);
        assertEquals("Continuum", softwareId.getName());
        assertEquals("1.0", softwareId.getVersion());
    }
}
