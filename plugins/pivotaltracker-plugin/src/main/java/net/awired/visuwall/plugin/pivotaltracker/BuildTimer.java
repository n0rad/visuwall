package net.awired.visuwall.plugin.pivotaltracker;

import java.util.Date;

import net.awired.clients.pivotaltracker.resource.Project;
import net.awired.visuwall.api.domain.BuildTime;

import org.joda.time.DateTime;

public class BuildTimer {

    private Project project;

    public BuildTimer(Project project) {
        this.project = project;
    }

    public BuildTime build() {
        BuildTime buildTime = new BuildTime();
        buildTime.setDuration(computeRemainingTime());
        return buildTime;
    }

    private long computeRemainingTime() {
        DateTime firstDayOfThisSprint = firstDayOfThisSprint();
        Date endDate = lastDayOfThisSprint(firstDayOfThisSprint);
        return endDate.getTime() - new Date().getTime();
    }

    private Date lastDayOfThisSprint(DateTime firstDayOfThisSprint) {
        Integer iterationLength = project.getIterationLength();
        return firstDayOfThisSprint.plusWeeks(iterationLength).toDate();
    }

    private DateTime firstDayOfThisSprint() {
        Date startDate = project.getFirstIterationStartTime();
        DateTime firstDayOfThisSprint = new DateTime(startDate.getTime());
        Integer iterationLength = project.getIterationLength();
        int numPastWeeks = project.getCurrentIterationNumber() - 1;
        int projectDuration = iterationLength * numPastWeeks;
        firstDayOfThisSprint = firstDayOfThisSprint.plusWeeks(projectDuration);
        return firstDayOfThisSprint;
    }

}
