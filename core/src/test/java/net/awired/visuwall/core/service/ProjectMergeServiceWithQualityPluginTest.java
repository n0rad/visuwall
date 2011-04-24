/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.awired.visuwall.core.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.QualityConnectionPlugin;
import net.awired.visuwall.core.service.ProjectMergeService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ProjectMergeServiceWithQualityPluginTest {

    ProjectMergeService projectMergeService = new ProjectMergeService();

    Project projectToMerge;

    @Before
    public void init() {
        ProjectId projectId = new ProjectId();
        projectId.addId("id", "value");
        projectToMerge = new Project(projectId);
    }

    @Test
    public void should_merge_with_one_build_plugin() throws ProjectNotFoundException {
        QualityConnectionPlugin qualityPlugin = Mockito.mock(QualityConnectionPlugin.class);

        QualityResult qualityResult = new QualityResult();
        QualityMeasure coverage = new QualityMeasure();
        coverage.setName("coverage");
        coverage.setValue(29D);
        coverage.setFormattedValue("29%");
        qualityResult.add("coverage", coverage);

        ProjectId projectId = projectToMerge.getProjectId();
        when(qualityPlugin.populateQuality(projectId)).thenReturn(qualityResult);
        projectMergeService.merge(projectToMerge, qualityPlugin);

        assertEquals(coverage, projectToMerge.getQualityResult().getMeasure("coverage"));
    }

    @Test
    public void should_merge_with_two_build_plugins() throws ProjectNotFoundException {
        QualityConnectionPlugin qualityPlugin1 = Mockito.mock(QualityConnectionPlugin.class);
        QualityConnectionPlugin qualityPlugin2 = Mockito.mock(QualityConnectionPlugin.class);

        QualityResult qualityResult1 = new QualityResult();
        QualityMeasure coverage = new QualityMeasure();
        coverage.setName("coverage");
        coverage.setValue(29D);
        coverage.setFormattedValue("29%");
        qualityResult1.add("coverage", coverage);

        QualityResult qualityResult2 = new QualityResult();
        QualityMeasure violations = new QualityMeasure();
        violations.setName("violations");
        violations.setValue(1D);
        violations.setFormattedValue("1%");
        qualityResult2.add("violations", violations);

        ProjectId projectId = projectToMerge.getProjectId();
        when(qualityPlugin1.populateQuality(projectId)).thenReturn(qualityResult1);
        when(qualityPlugin2.populateQuality(projectId)).thenReturn(qualityResult2);
        projectMergeService.merge(projectToMerge, qualityPlugin1);
        projectMergeService.merge(projectToMerge, qualityPlugin2);

        assertEquals(coverage, projectToMerge.getQualityResult().getMeasure("coverage"));
        assertEquals(violations, projectToMerge.getQualityResult().getMeasure("violations"));
    }

    @Test
    public void last_plugin_is_always_right() throws ProjectNotFoundException {
        QualityConnectionPlugin qualityPlugin1 = Mockito.mock(QualityConnectionPlugin.class);
        QualityConnectionPlugin qualityPlugin2 = Mockito.mock(QualityConnectionPlugin.class);

        QualityResult qualityResult1 = new QualityResult();
        QualityMeasure coverage = new QualityMeasure();
        coverage.setName("coverage");
        coverage.setValue(29D);
        coverage.setFormattedValue("29%");
        qualityResult1.add("coverage", coverage);

        QualityResult qualityResult2 = new QualityResult();
        QualityMeasure coverage2 = new QualityMeasure();
        coverage2.setName("coverage");
        coverage2.setValue(1D);
        coverage2.setFormattedValue("1%");
        qualityResult2.add("coverage", coverage2);

        ProjectId projectId = projectToMerge.getProjectId();
        when(qualityPlugin1.populateQuality(projectId)).thenReturn(qualityResult1);
        when(qualityPlugin2.populateQuality(projectId)).thenReturn(qualityResult2);
        projectMergeService.merge(projectToMerge, qualityPlugin1);
        projectMergeService.merge(projectToMerge, qualityPlugin2);

        assertEquals(coverage2, projectToMerge.getQualityResult().getMeasure("coverage"));

    }

}
