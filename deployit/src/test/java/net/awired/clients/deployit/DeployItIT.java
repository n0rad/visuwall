package net.awired.clients.deployit;

import static org.junit.Assert.assertNotNull;
import net.awired.clients.common.ResourceNotFoundException;
import net.awired.clients.deployit.resource.ArchivedTasks;

import org.junit.Test;

public class DeployItIT {

    @Test
    public void should_get_all_archived_tasks() throws ResourceNotFoundException {
        DeployIt deployIt = new DeployIt("http://localhost:4516", "admin", "admin");
        ArchivedTasks archivedTasks = deployIt.getArchivedTasks();
        assertNotNull(archivedTasks);
        System.out.println(archivedTasks.getTasks().size());
    }

}
