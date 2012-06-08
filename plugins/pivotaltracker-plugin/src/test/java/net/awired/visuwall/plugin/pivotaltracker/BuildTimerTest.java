package net.awired.visuwall.plugin.pivotaltracker;

import static org.junit.Assert.assertEquals;
import net.awired.clients.pivotaltracker.resource.Project;
import net.awired.visuwall.api.domain.BuildTime;

import org.junit.Test;

public class BuildTimerTest {

    @Test
    public void t() {
        Project project = new Project();
        project.setWeekStartDay("Monday");
        project.setIterationLength(2);
        project.setCurrentIterationNumber(5);
        project.setFirstIterationStartTime("2012/04/10 22:00:00 UTC");
        BuildTime buildTime = new BuildTimer(project).build();
        long remainingTime = buildTime.getDuration();
        long expectedRemainingTime = 1209600000;
        assertEquals(expectedRemainingTime, remainingTime);
    }

}
