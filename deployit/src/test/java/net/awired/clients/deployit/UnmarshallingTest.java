package net.awired.clients.deployit;

import static org.junit.Assert.assertEquals;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.awired.clients.deployit.resource.ArchivedTasks;
import net.awired.clients.deployit.resource.Task;

import org.junit.Test;

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
        assertEquals("Thu Feb 09 11:10:42 CET 2012", task.getCompletionDate().toString());
        assertEquals("Tomcat-Dev", task.getEnvironment());
        assertEquals(1, task.getFailureCount());
        assertEquals("Deployment of package:XkeTomcatPet version:9 to env:Tomcat-Dev", task.getLabel());
        assertEquals("Thu Feb 09 11:10:42 CET 2012", task.getStartDate().toString());
        assertEquals(State.DONE, task.getState());
        assertEquals("admin", task.getUser());
        assertEquals("9", task.getVersion());
    }

}
