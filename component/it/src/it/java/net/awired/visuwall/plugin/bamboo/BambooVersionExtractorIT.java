package net.awired.visuwall.plugin.bamboo;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import net.awired.visuwall.IntegrationTestData;

import org.junit.Test;

public class BambooVersionExtractorIT {

    @Test
    public void should_extract_version() throws Exception {
        URL url = new URL(IntegrationTestData.BAMBOO_URL);
        String version = BambooVersionExtractor.extractVersion(url);
        assertEquals("2.7.1", version);
    }

}
