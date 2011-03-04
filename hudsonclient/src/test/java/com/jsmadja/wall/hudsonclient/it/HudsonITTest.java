package com.jsmadja.wall.hudsonclient.it;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.jsmadja.wall.hudsonclient.Hudson;
import com.jsmadja.wall.hudsonclient.HudsonProjectNotFoundException;
import com.jsmadja.wall.hudsonclient.domain.HudsonProject;

public class HudsonITTest {

    private Hudson hudson = new Hudson("http://integration.wormee.orange-vallee.net:8080/hudson");

    @Test
    public void should_return_X_status() throws HudsonProjectNotFoundException {
        HudsonProject project = hudson.findProject("on-parameter-tester-staging");
        assertNotNull(project);
    }
}
