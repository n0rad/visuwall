package net.awired.clients.teamcity;

import static org.junit.Assert.assertEquals;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.awired.clients.teamcity.resource.TeamCityBuilds;

import org.junit.Test;

public class TeamCityBuildsTest {

    @Test
    public void should_get_all_builds() throws Exception {
        JAXBContext context = JAXBContext.newInstance(TeamCityBuilds.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        TeamCityBuilds builds = (TeamCityBuilds) unmarshaller.unmarshal(this.getClass().getClassLoader()
                .getResource("app/rest/builds/locator=running:true.xml"));

        assertEquals(2, builds.getCount());
        assertEquals(2, builds.getBuilds().size());
    }

}
