package fr.norad.visuwall.plugin.demo;

import static fr.norad.visuwall.plugin.demo.SoftwareProjectIds.moon;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import fr.norad.visuwall.api.domain.BuildTime;

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
