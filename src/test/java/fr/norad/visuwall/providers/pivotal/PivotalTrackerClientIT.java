/**
 *
 *     Copyright (C) norad.fr
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package fr.norad.visuwall.providers.pivotal;

import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import fr.norad.visuwall.providers.common.ResourceNotFoundException;
import fr.norad.visuwall.providers.pivotal.resource.Project;
import fr.norad.visuwall.providers.pivotal.resource.Projects;
import fr.norad.visuwall.providers.pivotal.resource.Stories;
import fr.norad.visuwall.providers.pivotal.resource.Story;

public class PivotalTrackerClientIT {

    private static PivotalTrackerClient client;

    @BeforeClass
    public static void init() {
        client = new PivotalTrackerClient("https://www.pivotaltracker.com", "", "");
    }

    @Test
    @Ignore("V3 is not working anymore ?")
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
    @Ignore("V3 is not working anymore ?")
    public void should_list_all_stories_of_a_project() throws ResourceNotFoundException {
        Stories stories = client.getStoriesOf(506819);
        Story story = stories.get(0);
        assertEquals("chore", story.getStoryType());
        assertEquals("accepted", story.getCurrentState());
    }

}
