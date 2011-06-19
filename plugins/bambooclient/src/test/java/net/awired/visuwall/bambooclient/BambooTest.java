package net.awired.visuwall.bambooclient;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import net.awired.visuwall.bambooclient.builder.BambooUrlBuilder;
import net.awired.visuwall.bambooclient.domain.BambooProject;
import net.awired.visuwall.bambooclient.rest.Plans;
import net.awired.visuwall.common.client.GenericSoftwareClient;
import net.awired.visuwall.common.client.ResourceNotFoundException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class BambooTest {

    BambooUrlBuilder bambooUrlBuilder;
    GenericSoftwareClient genericSoftwareClient;
    Bamboo bamboo;

    @Before
    public void init() {
        bambooUrlBuilder = Mockito.mock(BambooUrlBuilder.class);
        genericSoftwareClient = Mockito.mock(GenericSoftwareClient.class);
        bamboo = new Bamboo("http://bamboo.com");
        bamboo.bambooUrlBuilder = bambooUrlBuilder;
        bamboo.client = genericSoftwareClient;
    }

    @Ignore
    @Test
    public void should_find_all_projects() throws ResourceNotFoundException {
        Plans plans = createPlans();
        when(genericSoftwareClient.resource(anyString(), Plans.class)).thenReturn(plans);

        List<BambooProject> projects = bamboo.findAllProjects();

        assertFalse(projects.isEmpty());
    }

    private Plans createPlans() {
        return (Plans) ClasspathFiles.load("rest/api/lastest/plan.xml", Plans.class);
    }

}
