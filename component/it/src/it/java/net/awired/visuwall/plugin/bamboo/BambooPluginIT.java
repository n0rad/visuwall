package net.awired.visuwall.plugin.bamboo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.MalformedURLException;
import java.net.URL;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.SoftwareId;

import org.junit.Test;

public class BambooPluginIT {

    @Test
    public void should_be_manageable() throws MalformedURLException {
        BambooPlugin bamboo = new BambooPlugin();
        SoftwareId softwareId = bamboo.getSoftwareId(new URL(IntegrationTestData.BAMBOO_URL));
        assertNotNull(softwareId);
        assertEquals("Bamboo", softwareId.getName());
        assertEquals("2.7.1", softwareId.getVersion());
        assertNull(softwareId.getWarnings());
    }
}
