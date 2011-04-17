package net.awired.visuwall.server.domain;

import java.util.Collection;

import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.server.domain.Software;
import net.awired.visuwall.server.domain.SoftwareAccess;
import net.awired.visuwall.server.domain.Wall;

import org.junit.Test;

public class OrangeValleeWallIT {

    @Test
    public void should_not_fail() {
        Wall wall = new Wall("test");
        wall.addSoftwareAccess(new SoftwareAccess(Software.HUDSON, "http://10.2.40.60/lifeisbetteron/jenkins"));
        wall.discoverProjects();

        Collection<Project> projects = wall.getProjects();

        for(Project project:projects) {
            System.err.println(project);
        }
    }

}
