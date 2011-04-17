package net.awired.visuwall.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import net.awired.visuwall.plugin.hudson.HudsonConnectionPlugin;
import net.awired.visuwall.plugin.sonar.SonarConnectionPlugin;
import net.awired.visuwall.server.domain.Software;
import net.awired.visuwall.server.exception.NotCreatedException;
import net.awired.visuwall.server.exception.SoftwareNotFoundException;
import net.awired.visuwall.server.service.SoftwareService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:/META-INF/spring/*-context.xml"})
public class SoftwareServiceIT {

    @Autowired
    SoftwareService softwareService;

    Logger LOG = LoggerFactory.getLogger(SoftwareServiceIT.class);

    @Test
    public void should_find_all_software() throws NotCreatedException {
        Software software1 = new Software("software1", HudsonConnectionPlugin.class, true, true);
        Software software2 = new Software("software2", SonarConnectionPlugin.class, true, true);
        softwareService.persist(software1);
        softwareService.persist(software2);

        List<Software> softwares = softwareService.findAll();
        assertTrue(softwares.contains(software1));
        assertTrue(softwares.contains(software2));
    }

    @Test
    public void should_persist_software() throws SoftwareNotFoundException, NotCreatedException {
        String name = "hudson";
        String className = HudsonConnectionPlugin.class.getName();
        boolean buildSoftware = true;
        boolean qualitySoftware = false;
        Software software = new Software(name, HudsonConnectionPlugin.class, buildSoftware, qualitySoftware);
        softwareService.persist(software);

        Software persistedSoftware = softwareService.find(name);

        assertEquals(name, persistedSoftware.getName());
        assertEquals(className, persistedSoftware.getClassName());
        assertEquals(buildSoftware, persistedSoftware.isBuildSoftware());
        assertEquals(qualitySoftware, persistedSoftware.isQualitySoftware());
    }

    @Test(expected = NullPointerException.class)
    public void should_throw_exception_when_passing_null_to_persist() throws NotCreatedException {
        softwareService.persist(null);
    }

    @Test
    public void should_find_a_software() throws SoftwareNotFoundException, NotCreatedException {
        Software mysoftware = new Software("mysoftware", HudsonConnectionPlugin.class, true, true);
        softwareService.persist(mysoftware);
        softwareService.find("mysoftware");
    }

    @Test(expected=SoftwareNotFoundException.class)
    public void should_throw_exception_when_searching_an_inexistant_software() throws SoftwareNotFoundException {
        softwareService.find("does.not.exist");
    }

    @Test(expected=NullPointerException.class)
    public void should_throw_exception_when_passing_null()  throws SoftwareNotFoundException {
        softwareService.find(null);
    }

    @Test(expected = Exception.class)
    public void should_throw_exception_when_trying_to_persist_already_existing_software() throws NotCreatedException {
        String name = UUID.randomUUID().toString();
        softwareService.persist(new Software(name, HudsonConnectionPlugin.class, true, true));
        softwareService.persist(new Software(name, HudsonConnectionPlugin.class, true, true));
    }
}
