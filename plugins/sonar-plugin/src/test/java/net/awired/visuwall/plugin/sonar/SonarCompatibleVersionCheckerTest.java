package net.awired.visuwall.plugin.sonar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SonarCompatibleVersionCheckerTest {

    SonarCompatibleVersionChecker checker = new SonarCompatibleVersionChecker(2.4);

    @Test
    public void should_get_incompatible_for_1_0_version() {
        assertFalse(checker.versionIsCompatible(1.0));
    }

    @Test
    public void should_get_incompatible_for_2_0_version() {
        assertFalse(checker.versionIsCompatible(2.0));
    }

    @Test
    public void should_get_incompatible_for_2_3_version() {
        assertFalse(checker.versionIsCompatible(2.3));
    }

    @Test
    public void should_get_compatible_for_3_0_version() {
        assertTrue(checker.versionIsCompatible(3.0));
    }

    @Test
    public void should_get_compatible_for_2_4_version() {
        assertTrue(checker.versionIsCompatible(2.4));
    }

    @Test
    public void should_get_compatible_for_2_5_version() {
        assertTrue(checker.versionIsCompatible(2.5));
    }

    @Test
    public void should_get_compatible_for_2_12_version() {
        assertTrue(checker.versionIsCompatible(2.12));
    }

    @Test
    public void should_get_compatible_for_2_4_version_as_string() {
        assertTrue(checker.versionIsCompatible("2.4"));
    }

    @Test
    public void should_get_compatible_for_2_12_version_as_string() {
        assertTrue(checker.versionIsCompatible("2.12"));
    }
}
