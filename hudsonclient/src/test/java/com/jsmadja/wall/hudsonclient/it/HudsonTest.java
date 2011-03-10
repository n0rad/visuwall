package com.jsmadja.wall.hudsonclient.it;

import org.junit.Test;

import com.jsmadja.wall.hudsonclient.Hudson;
import com.jsmadja.wall.hudsonclient.HudsonBuildNotFoundException;
import com.jsmadja.wall.hudsonclient.HudsonProjectNotFoundException;

public class HudsonTest {

    private static final String FLUXX_HUDSON_URL = "http://fluxx.fr.cr:8080/hudson";
    private static final String AWIRED_HUDSON_URL = "http://ci.awired.net/jenkins";

    private Hudson hudsonFluxx = new Hudson(FLUXX_HUDSON_URL);
    private Hudson hudsonAwired = new Hudson(AWIRED_HUDSON_URL);

    @Test(expected = HudsonBuildNotFoundException.class)
    public void should_throw_an_exception_when_searching_an_inexistant_build() throws HudsonBuildNotFoundException {
        hudsonFluxx.findBuild("dev-radar", -1);
    }

    @Test
    public void should_find_not_built_project() throws HudsonProjectNotFoundException {
        hudsonAwired.findProject("ajsl");
    }

}
