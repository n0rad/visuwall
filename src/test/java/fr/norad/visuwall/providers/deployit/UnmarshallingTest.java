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
package fr.norad.visuwall.providers.deployit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.junit.Test;
import fr.norad.visuwall.providers.deployit.resource.ArchivedTasks;
import fr.norad.visuwall.providers.deployit.resource.Task;

public class UnmarshallingTest {

    @Test
    public void should_unmarshall_archived_tasks() throws JAXBException {
        JAXBContext newInstance = JAXBContext.newInstance(ArchivedTasks.class);
        Unmarshaller unmarshaller = newInstance.createUnmarshaller();
        ArchivedTasks archivedTasks = (ArchivedTasks) unmarshaller.unmarshal(UnmarshallingTest.class.getClassLoader()
                .getResourceAsStream("export/tasks/archived-tasks.xml"));
        assertEquals(39, archivedTasks.getTasks().size());
        Task task = archivedTasks.getTasks().get(0);
        assertEquals("XkeTomcatPet", task.getApplication());
        assertNotNull(task.getCompletionDate().toString());
        assertEquals("Tomcat-Dev", task.getEnvironment());
        assertEquals(1, task.getFailureCount());
        assertEquals("Deployment of package:XkeTomcatPet version:9 to env:Tomcat-Dev", task.getLabel());
        assertNotNull(task.getStartDate().toString());
        assertEquals(State.DONE, task.getState());
        assertEquals("admin", task.getUser());
        assertEquals(9, task.getVersion().intValue());
    }
}
