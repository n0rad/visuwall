package net.awired.visuwall.plugin.bamboo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BambooVersionExtractorTest {

    @Test
    public void should_extract_version() throws Exception {
        String content = "Atlassian Bamboo</a> version 2.7.1 build 2101 -";
        String version = BambooVersionExtractor.extractVersion(content);
        assertEquals("2.7.1", version);
    }

    @Test(expected = BambooVersionNotFoundException.class)
    public void should_throw_exception_if_version_is_not_found() throws BambooVersionNotFoundException {
        BambooVersionExtractor.extractVersion("");
    }

}
