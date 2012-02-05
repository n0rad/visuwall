package net.awired.visuwall.plugin.teamcity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestResultExtractorTest {

    @Test
    public void should_extract_all() {
        String statusText = "Tests failed: 1 (1 new), passed: 3, ignored: 5";

        int failed = TestResultExtractor.extractFailed(statusText);
        int passed = TestResultExtractor.extractPassed(statusText);
        int ignored = TestResultExtractor.extractIgnored(statusText);

        assertEquals(1, failed);
        assertEquals(3, passed);
        assertEquals(5, ignored);
    }

    @Test
    public void should_extract_passed() {
        String statusText = "Tests passed: 331";

        int failed = TestResultExtractor.extractFailed(statusText);
        int passed = TestResultExtractor.extractPassed(statusText);
        int ignored = TestResultExtractor.extractIgnored(statusText);

        assertEquals(0, failed);
        assertEquals(331, passed);
        assertEquals(0, ignored);
    }
}
