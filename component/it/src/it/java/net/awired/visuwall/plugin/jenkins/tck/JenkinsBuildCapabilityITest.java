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

package net.awired.visuwall.plugin.jenkins.tck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.api.plugin.tck.BuildCapabilityTCK;
import net.awired.visuwall.plugin.jenkins.JenkinsConnection;

import org.junit.Before;
import org.junit.Test;


public class JenkinsBuildCapabilityITest implements BuildCapabilityTCK {

	BuildCapability jenkins = new JenkinsConnection();

	@Before
    public void init() throws ConnectionException {
		((Connection) jenkins).connect(IntegrationTestData.JENKINS_URL, null, null);
	}

	@Override
	@Test
	public void should_find_build_by_build_number() throws BuildNotFoundException, ProjectNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(JenkinsConnection.JENKINS_ID, "struts");
		Build build = jenkins.findBuildByBuildNumber(projectId, 3);
		assertNotNull(build);
		assertEquals(3, build.getBuildNumber());
		assertEquals(State.SUCCESS, build.getState());
	}

	@Override
	@Test
	public void should_get_last_build_number() throws ProjectNotFoundException, BuildNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(JenkinsConnection.JENKINS_ID, "struts");
		int number = jenkins.getLastBuildNumber(projectId);
		assertEquals(4, number);
	}

	@Override
	@Test
	public void should_get_success_build_state() throws ProjectNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(JenkinsConnection.JENKINS_ID, "struts");
		State state = jenkins.getLastBuildState(projectId);
		assertEquals(State.SUCCESS, state);
	}

	@Override
	@Test
	public void should_get_is_building() throws ProjectNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(JenkinsConnection.JENKINS_ID, "struts");
		boolean isBuilding = jenkins.isBuilding(projectId);
		assertFalse(isBuilding);
	}

	@Override
	@Test
	public void should_get_estimated_date() throws ProjectNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(JenkinsConnection.JENKINS_ID, "struts");
		Date date = jenkins.getEstimatedFinishTime(projectId);
		assertNotNull(date);
	}
}
