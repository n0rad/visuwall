package net.awired.visuwall.core.service;

import java.util.Arrays;
import java.util.Map.Entry;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.BuildConnectionPlugin;
import net.awired.visuwall.api.plugin.QualityConnectionPlugin;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

@Service
public class ProjectMergeService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectMergeService.class);

    public void merge(Project projectToMerge, QualityConnectionPlugin qualityPlugin, String... metrics) {
        QualityResult qualityResult = qualityPlugin.populateQuality(projectToMerge.getProjectId(), metrics);
        if (qualityResult != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("plugin - " + qualityPlugin.getClass().getSimpleName());
            }
            for (Entry<String, QualityMeasure> entry : qualityResult.getMeasures()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(entry.getKey()+" - "+entry.getValue());
                }
                projectToMerge.getQualityResult().add(entry.getKey(), entry.getValue());
            }
        }
    }

    public void merge(Project projectToMerge, BuildConnectionPlugin buildPlugin) {
        ProjectId projectId = projectToMerge.getProjectId();
        Preconditions.checkState(projectId != null, "projectToComplete must have a projectId");
        try {
            Project project = buildPlugin.findProject(projectId);
            if (project != null) {
                String projectName = project.getName();
                String description = project.getDescription();
                int[] buildNumbers = project.getBuildNumbers();
                Build completedBuild = project.getCompletedBuild();
                Build currentBuild = project.getCurrentBuild();
                State state = project.getState();

                if (LOG.isDebugEnabled()) {
                    LOG.debug("plugin - "+buildPlugin.getClass().getSimpleName());
                    LOG.debug("projectName:"+ projectName);
                    LOG.debug("description:"+ description);
                    LOG.debug("buildNumbers:"+ Arrays.toString(buildNumbers));
                    LOG.debug("completedBuild:"+ completedBuild);
                    LOG.debug("currentBuild: "+currentBuild);
                    LOG.debug("state:"+ state);
                }

                if (buildNumbers != null) {
                    projectToMerge.setBuildNumbers(buildNumbers);
                }
                if (completedBuild != null) {
                    projectToMerge.setCompletedBuild(completedBuild);
                }
                if (currentBuild != null) {
                    projectToMerge.setCurrentBuild(currentBuild);
                }
                if (StringUtils.isNotBlank(description)) {
                    projectToMerge.setDescription(description);
                }
                if (StringUtils.isNotBlank(projectName)) {
                    projectToMerge.setName(projectName);
                }
                if (state != null) {
                    projectToMerge.setState(state);
                }
            }
        } catch(ProjectNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage());
            }
        }
    }

}
