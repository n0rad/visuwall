/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
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

package net.awired.visuwall.plugin.jenkins;

import static net.awired.visuwall.api.domain.State.DISABLED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URL;
import java.util.List;
import java.util.Map;

import net.awired.visuwall.Urls;
import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;

import org.junit.Test;

public class JenkinsPluginIT {

    JenkinsPlugin jenkinsPlugin = new JenkinsPlugin();

    @Test
    public void should_recognize_jenkins_instance_with_valid_url() throws Exception {
        SoftwareId softwareId = jenkinsPlugin.getSoftwareId(new URL(Urls.AWIRED_JENKINS));

        assertEquals("Jenkins", softwareId.getName());
        assertEquals("1.407", softwareId.getVersion());
        assertNull(softwareId.getWarnings());
    }

    @Test(expected = IncompatibleSoftwareException.class)
    public void should_not_fail_if_url_is_not_manageable() throws Exception {
        jenkinsPlugin.getSoftwareId(new URL("http://www.google.fr"));
    }

    @Test
    public void should_check_plugin_management() throws Exception {
        URL hudsonUrl = new URL("http://ci.awired.net/jenkins/");
        JenkinsConnection connection = jenkinsPlugin.getConnection(hudsonUrl,
                jenkinsPlugin.getPropertiesWithDefaultValue());
        SoftwareProjectId projectId = new SoftwareProjectId("test42");
        State buildState = connection.getBuildState(projectId, "1");
        assertEquals(DISABLED, buildState);
    }

    @Test(timeout = 5000)
    public void should_get_all_projects() throws Exception {
        URL hudsonUrl = new URL("http://hudson.jboss.org/jenkins/");
        JenkinsConnection connection = jenkinsPlugin.getConnection(hudsonUrl,
                jenkinsPlugin.getPropertiesWithDefaultValue());
        List<String> views = connection.findViews();
        System.out.println(views.size() + " views");
        Map<SoftwareProjectId, String> softwareProjectIds = connection.listSoftwareProjectIds();
        System.out.println(softwareProjectIds.size() + " projects");
    }

}
