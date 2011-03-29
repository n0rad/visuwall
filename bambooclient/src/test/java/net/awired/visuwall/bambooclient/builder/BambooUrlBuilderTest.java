package net.awired.visuwall.bambooclient.builder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class BambooUrlBuilderTest {

    private String bambooUrl = "http://bamboo.visuwall.awired.net";
    private BambooUrlBuilder builder = new BambooUrlBuilder(bambooUrl);

    @Test
    public void should_build_all_projects_url() {
        String expectedUrl = "http://bamboo.visuwall.awired.net/rest/api/latest/plan";
        assertEquals(expectedUrl, builder.getAllProjectsUrl());
    }

}
