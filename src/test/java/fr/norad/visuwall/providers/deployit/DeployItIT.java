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

import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;
import org.junit.Test;
import fr.norad.visuwall.providers.common.ResourceNotFoundException;
import fr.norad.visuwall.providers.deployit.resource.ArchivedTasks;

public class DeployItIT {

    @Test
    @Ignore("no deployit online demo")
    public void should_get_all_archived_tasks() throws ResourceNotFoundException {
        DeployIt deployIt = new DeployIt("http://localhost:4516", "admin", "admin");
        ArchivedTasks archivedTasks = deployIt.getArchivedTasks();
        assertNotNull(archivedTasks);
        System.out.println(archivedTasks.getTasks().size());
    }

}
