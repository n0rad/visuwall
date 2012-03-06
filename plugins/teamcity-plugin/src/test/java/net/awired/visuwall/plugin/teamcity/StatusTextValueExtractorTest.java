package net.awired.visuwall.plugin.teamcity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StatusTextValueExtractorTest {

    StatusTextValueExtractor statusTextValueExtractor = new StatusTextValueExtractor();

    @Test
    public void should_extract_all() {
        String statusText = "Tests failed: 1 (1 new), passed: 3, ignored: 5";

        int failed = statusTextValueExtractor.extract(statusText, "failed");
        int passed = statusTextValueExtractor.extract(statusText, "passed");
        int ignored = statusTextValueExtractor.extract(statusText, "ignored");

        assertEquals(1, failed);
        assertEquals(3, passed);
        assertEquals(5, ignored);
    }

    @Test
    public void should_return_value_when_key_contains_special_chars() {
        String statusText = "Tests failed!: 1, passed?: 3, ignored|: 5";

        int failed = statusTextValueExtractor.extract(statusText, "failed!");
        int passed = statusTextValueExtractor.extract(statusText, "passed?");
        int ignored = statusTextValueExtractor.extract(statusText, "ignored|");

        assertEquals(1, failed);
        assertEquals(3, passed);
        assertEquals(5, ignored);
    }
}
