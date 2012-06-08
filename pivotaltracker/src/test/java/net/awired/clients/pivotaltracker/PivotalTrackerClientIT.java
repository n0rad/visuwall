package net.awired.clients.pivotaltracker;

import static org.junit.Assert.assertEquals;
import net.awired.clients.common.ResourceNotFoundException;
import net.awired.clients.pivotaltracker.resource.Project;
import net.awired.clients.pivotaltracker.resource.Projects;
import net.awired.clients.pivotaltracker.resource.Stories;
import net.awired.clients.pivotaltracker.resource.Story;

import org.junit.BeforeClass;
import org.junit.Test;

public class PivotalTrackerClientIT {

    private static PivotalTrackerClient client;

    @BeforeClass
    public static void init() {
        client = new PivotalTrackerClient("https://www.pivotaltracker.com", "jsmadja@financeactive.com", "xedy4bsa");
    }

    @Test
    public void should_list_all_projects() throws Exception {
        Projects projects = client.getProjects();

        assertEquals(1, projects.getProjects().size());
        Project project = projects.getProjects().get(0);
        assertEquals(506819, project.getId().intValue());
        assertEquals("Caution", project.getName());
        System.err.println(project.getWeekStartDay());
        System.err.println(project.getIterationLength());
        System.err.println(project.getLastActivityAt());
    }

    @Test
    public void should_list_all_stories_of_a_project() throws ResourceNotFoundException {
        Stories stories = client.getStoriesOf(506819);
        Story story = stories.get(0);
        assertEquals("chore", story.getStoryType());
        assertEquals("accepted", story.getCurrentState());
    }

}
