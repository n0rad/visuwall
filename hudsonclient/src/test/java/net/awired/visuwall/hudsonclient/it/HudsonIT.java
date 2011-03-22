package net.awired.visuwall.hudsonclient.it;

import net.awired.visuwall.hudsonclient.Hudson;
import net.awired.visuwall.hudsonclient.HudsonBuildNotFoundException;
import net.awired.visuwall.hudsonclient.HudsonProjectNotFoundException;

import org.junit.Test;


public class HudsonIT {

    private Hudson hudson = new Hudson(IntegrationTestData.HUDSON_URL);

    @Test(expected = HudsonBuildNotFoundException.class)
    public void should_throw_an_exception_when_searching_an_inexistant_build() throws HudsonBuildNotFoundException, HudsonProjectNotFoundException {
        hudson.findBuild("inexist", -1);
    }

    @Test
    public void should_find_not_built_project() throws HudsonProjectNotFoundException {
        hudson.findProject("neverbuild");
    }

    @Test(expected=HudsonProjectNotFoundException.class)
    public void should_throw_exception_when_searching_inexistant_project() throws HudsonProjectNotFoundException {
        hudson.findProject("");
    }

    @Test(expected=HudsonBuildNotFoundException.class)
    public void should_throw_exception_when_searching_inexistant_build() throws HudsonBuildNotFoundException, HudsonProjectNotFoundException {
        hudson.findBuild("", 0);
    }

}
