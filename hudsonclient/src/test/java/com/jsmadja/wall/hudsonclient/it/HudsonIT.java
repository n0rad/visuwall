package com.jsmadja.wall.hudsonclient.it;

import org.junit.Test;

import com.jsmadja.wall.hudsonclient.Hudson;
import com.jsmadja.wall.hudsonclient.HudsonBuildNotFoundException;
import com.jsmadja.wall.hudsonclient.HudsonProjectNotFoundException;

public class HudsonIT {

    private static final String HUDSON_URL = "http://ci.jwall.awired.net";

    private Hudson hudson = new Hudson(HUDSON_URL);

    @Test(expected = HudsonBuildNotFoundException.class)
    public void should_throw_an_exception_when_searching_an_inexistant_build() throws HudsonBuildNotFoundException {
        hudson.findBuild("dev-radar", -1);
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
    public void should_throw_exception_when_searching_inexistant_build() throws HudsonBuildNotFoundException {
        hudson.findBuild("", 0);
    }

}
