package net.awired.clients.hudson.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class HudsonTestTest {

    @Test
    public void is_it_when_package_contains_it() {
        HudsonTest hudsonTest = new HudsonTest("net.awired.it.MyTest", "SUCCESS");
        assertTrue(hudsonTest.isIntegrationTest());
    }

    @Test
    public void is_it_when_ends_with_IT() {
        HudsonTest hudsonTest = new HudsonTest("net.awired.MyIT", "SUCCESS");
        assertTrue(hudsonTest.isIntegrationTest());
    }

    @Test
    public void is_it_when_ends_with_ITest() {
        HudsonTest hudsonTest = new HudsonTest("net.awired.MyITest", "SUCCESS");
        assertTrue(hudsonTest.isIntegrationTest());
    }

    @Test
    public void is_it_when_ends_with_IntegrationTest() {
        HudsonTest hudsonTest = new HudsonTest("net.awired.MyIntegrationTest", "SUCCESS");
        assertTrue(hudsonTest.isIntegrationTest());
    }

    @Test
    public void is_ut() {
        HudsonTest hudsonTest = new HudsonTest("net.awired.MyTest", "SUCCESS");
        assertTrue(hudsonTest.isUnitTest());
        assertFalse(hudsonTest.isIntegrationTest());
    }

}
