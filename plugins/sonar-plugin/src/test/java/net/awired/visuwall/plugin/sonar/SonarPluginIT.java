package net.awired.visuwall.plugin.sonar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import net.awired.visuwall.api.domain.SoftwareId;

import org.junit.Test;

public class SonarPluginIT {

    SonarPlugin plugin = new SonarPlugin();

    @Test
    public void should_recognize_sonar_v212() throws Exception {
        SoftwareId softwareId = plugin.getSoftwareId(new URL("http://localhost:9000"));
        assertEquals("Sonar", softwareId.getName());
        assertEquals("2.12", softwareId.getVersion());
        assertTrue(softwareId.isCompatible());
        assertNull(softwareId.getWarnings());
    }

}
