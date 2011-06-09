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

package net.awired.visuwall.api.plugin;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.NotImplementedOperationException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.exception.ViewNotFoundException;

public interface ConnectionPlugin {

	/**
	 * Generate a complete quality reporting for a project defined by <code>projectId</code>
	 * 
	 * @param projectId
	 * @param metrics
	 *            You can specify the metrics you only want to analyze.
	 * @return
	 */
	QualityResult analyzeQuality(ProjectId projectId, String... metrics) throws NotImplementedOperationException;

	/**
	 * Generate the unit tests reporting
	 * 
	 * @param projectId
	 * @return
	 */
	TestResult analyzeUnitTests(ProjectId projectId) throws NotImplementedOperationException;

	/**
	 * Generate the integration tests reporting
	 * 
	 * @param projectId
	 * @return
	 */
	TestResult analyzeIntegrationTests(ProjectId projectId) throws NotImplementedOperationException;

	/**
	 * @param projectId
	 * @throws NotImplementedOperationException
	 * @returntrue if the project is in the Quality Software
	 */
	boolean contains(ProjectId projectId) throws NotImplementedOperationException;

	/**
	 * @return Quality Metrics sorted by category
	 */
	Map<String, List<QualityMetric>> getMetricsByCategory() throws NotImplementedOperationException;

	/**
	 * Return the full list of project id contained in the software
	 * 
	 * @return
	 */
	List<ProjectId> findAllProjects() throws NotImplementedOperationException;

	/**
	 * Plugin should be able to retrieve projects by theirs projectId. ProjectId are filled when you call
	 * findAllProjects for example
	 * 
	 * @param projectId
	 * @return
	 * @throws ProjectNotFoundException
	 */
	Project findProject(ProjectId projectId) throws NotImplementedOperationException, ProjectNotFoundException;

	/**
	 * Build software can order their builds by number, plugin should be able to retrieve builds by number too
	 * 
	 * @param projectId
	 * @param buildNumber
	 * @return
	 * @throws BuildNotFoundException
	 * @throws ProjectNotFoundException
	 */
	Build findBuildByBuildNumber(ProjectId projectId, int buildNumber) throws NotImplementedOperationException,
	        BuildNotFoundException,
	        ProjectNotFoundException;

	/**
	 * Populating a project means fill its attributes with all data that you can fetch from your system If you can fetch
	 * State or Build informations, add it in <code>project</code>!
	 * 
	 * @param project
	 *            Project to populate
	 * @throws ProjectNotFoundException
	 *             Throw this exception if you can't find this project in your system
	 */
	void populate(Project project) throws NotImplementedOperationException, ProjectNotFoundException;

	/**
	 * If a project is building, plugin can calculate the estimated finish time
	 * 
	 * @param projectId
	 * @return
	 * @throws ProjectNotFoundException
	 */
	Date getEstimatedFinishTime(ProjectId projectId) throws NotImplementedOperationException, ProjectNotFoundException;

	/**
	 * Return true if project is building
	 * 
	 * @param projectId
	 * @return
	 * @throws ProjectNotFoundException
	 */
	boolean isBuilding(ProjectId projectId) throws NotImplementedOperationException, ProjectNotFoundException;

	/**
	 * Project are in a certain state which may vary between software You'll have to try to associate them with common
	 * States
	 * 
	 * @param projectId
	 * @return
	 * @throws ProjectNotFoundException
	 */
	State getState(ProjectId projectId) throws NotImplementedOperationException, ProjectNotFoundException;

	/**
	 * Return the last build number of a project
	 * 
	 * @param projectId
	 * @return
	 * @throws ProjectNotFoundException
	 * @throws BuildNotFoundException
	 */
	int getLastBuildNumber(ProjectId projectId) throws NotImplementedOperationException, ProjectNotFoundException,
	        BuildNotFoundException;

	/**
	 * Find all project names of projects handle by the software
	 * 
	 * @return
	 */
	List<String> findProjectNames() throws NotImplementedOperationException;

	/**
	 * Software can sort projects by views, or graphically by tabs. If so, plugin can list these views.
	 * 
	 * @return List of view names
	 */
	List<String> findViews() throws NotImplementedOperationException;

	/**
	 * If software sorts its projects by view, you should be able to retrieve project names by view name
	 * 
	 * @param viewName
	 * @return List of project names contained in view
	 * @throws ViewNotFoundException
	 */
	List<String> findProjectsByView(String viewName) throws NotImplementedOperationException, ViewNotFoundException;

}