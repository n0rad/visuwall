package net.awired.clients.deployit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DeployItUrlBuilderTest {

    DeployItUrlBuilder urlBuilder = new DeployItUrlBuilder("http://localhost:4516");

    @Test
    public void should_build_archived_tasks_url() {
        assertEquals("http://localhost:4516/deployit/export/tasks", urlBuilder.getArchivedTasksUrl());
    }

}
