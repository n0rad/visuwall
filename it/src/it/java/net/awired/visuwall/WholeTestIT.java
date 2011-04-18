package net.awired.visuwall;

import static org.junit.Assert.assertFalse;

import java.util.List;

import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.core.domain.SoftwareAccess;
import net.awired.visuwall.core.domain.Wall;
import net.awired.visuwall.core.exception.NotCreatedException;
import net.awired.visuwall.core.exception.NotFoundException;
import net.awired.visuwall.core.service.WallService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:/META-INF/spring/*-context.xml"})
public class WholeTestIT {

    @Autowired
    WallService wallService;

    @Test
    public void classic_test() throws NotCreatedException, NotFoundException {

        Wall newwall = new Wall("standard wall");
        List<SoftwareAccess> softwareAccesses = newwall.getSoftwareAccesses();
        softwareAccesses.add(IntegrationTestData.HUDSON_ACCESS);
        softwareAccesses.add(IntegrationTestData.SONAR_ACCESS);

        wallService.persist(newwall);

        Wall wall = wallService.find("standard wall");

        List<Project> projects = wall.getProjects();

        assertFalse(projects.isEmpty());
    }
}
