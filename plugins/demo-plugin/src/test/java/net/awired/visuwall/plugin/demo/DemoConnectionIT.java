package net.awired.visuwall.plugin.demo;

import static net.awired.visuwall.plugin.demo.SoftwareProjectIds.moon;

import java.util.concurrent.TimeUnit;

import net.awired.visuwall.api.domain.BuildTime;

import org.junit.Test;

public class DemoConnectionIT {

    @Test
    public void should_() throws Exception {

        DemoConnection connection = new DemoConnection();
        connection.connect("", "", "");
        String buildId = connection.getLastBuildId(moon);
        while (true) {
            BuildTime buildTime = connection.getBuildTime(moon, buildId);
            System.out.println("---");
            System.out.println(buildTime.getStartTime());
            System.out.println(buildTime.getDuration());
            TimeUnit.SECONDS.sleep(1);
        }
    }

}
