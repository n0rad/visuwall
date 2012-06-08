package net.awired.clients.pivotaltracker;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PivotalTrackerUrlBuilderTest {

    private PivotalTrackerUrlBuilder builder = new PivotalTrackerUrlBuilder("https://www.pivotaltracker.com");

    @Test
    public void build_authentication_token_url() {
        assertEquals("https://www.pivotaltracker.com/services/v3/tokens/active", builder.getAuthenticationTokenUrl());
    }

    @Test
    public void build_all_projects_url() {
        assertEquals("https://www.pivotaltracker.com/services/v3/projects", builder.getAllProjectsUrl());
    }

    @Test
    public void build_project_url() {
        assertEquals("https://www.pivotaltracker.com/services/v3/projects/1234", builder.getProjectUrl(1234));
    }

    @Test
    public void build_all_stories_url() {
        assertEquals("https://www.pivotaltracker.com/services/v3/projects/506819/stories",
                builder.getAllStoriesUrl(506819));
    }

}
