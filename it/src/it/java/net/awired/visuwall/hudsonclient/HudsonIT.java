package net.awired.visuwall.hudsonclient;

import net.awired.visuwall.IntegrationTestData;

import org.junit.Test;


public class HudsonIT {

    private Hudson hudson = new Hudson(IntegrationTestData.HUDSON_URL);

    @Test(expected = HudsonBuildNotFoundException.class)
    public void should_throw_an_exception_when_searching_an_inexistant_build() throws HudsonBuildNotFoundException, HudsonProjectNotFoundException {
        hudson.findBuild("neverbuild", -1);
    }

    @Test
    public void should_find_not_built_project() throws HudsonProjectNotFoundException {
        hudson.findProject("neverbuild");
    }

    @Test(expected=HudsonProjectNotFoundException.class)
    public void should_throw_exception_when_searching_inexistant_project() throws HudsonProjectNotFoundException {
        hudson.findProject("");
    }

    @Test(expected=HudsonProjectNotFoundException.class)
    public void should_throw_exception_when_searching_inexistant_project_with_build_no() throws HudsonProjectNotFoundException, HudsonBuildNotFoundException {
        hudson.findBuild("", 0);
    }

}
