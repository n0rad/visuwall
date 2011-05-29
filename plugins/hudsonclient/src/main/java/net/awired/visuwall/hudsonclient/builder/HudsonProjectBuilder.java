package net.awired.visuwall.hudsonclient.builder;

import java.util.List;

import net.awired.visuwall.hudsonclient.ArtifactIdNotFoundException;
import net.awired.visuwall.hudsonclient.HudsonBuildNotFoundException;
import net.awired.visuwall.hudsonclient.HudsonFinder;
import net.awired.visuwall.hudsonclient.HudsonProjectNotFoundException;
import net.awired.visuwall.hudsonclient.HudsonRootModuleFinder;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonProject;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonMavenMavenModuleSet;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonModelJob;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonModelRun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

public class HudsonProjectBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(HudsonProjectBuilder.class);

	private static final int UNBUILT_PROJECT = -1;

	private HudsonRootModuleFinder hudsonRootModuleFinder;

	private HudsonFinder hudsonBuildFinder;

	public HudsonProjectBuilder(HudsonUrlBuilder hudsonUrlBuilder, HudsonFinder hudsonBuildFinder) {
		hudsonRootModuleFinder = new HudsonRootModuleFinder(hudsonUrlBuilder);
		this.hudsonBuildFinder = hudsonBuildFinder;
	}

	public HudsonProject createHudsonProjectFrom(
			HudsonMavenMavenModuleSet moduleSet)
			throws HudsonBuildNotFoundException, HudsonProjectNotFoundException {
		HudsonBuild lastCompletedHudsonBuild = null, currentHudsonBuild = null;

		int lastCompleteBuildNumber = UNBUILT_PROJECT, currentBuildNumber = UNBUILT_PROJECT;
		String name = moduleSet.getName();
		String description = moduleSet.getDescription();
		String artifactId = artifactId(name);

		// current build number
		HudsonModelRun currentHudsonRun = moduleSet.getLastBuild();
		if (currentHudsonRun != null) {
			currentBuildNumber = currentHudsonRun.getNumber();
		}
		if (currentBuildNumber != UNBUILT_PROJECT) {
			currentHudsonBuild = hudsonBuildFinder.find(name, currentBuildNumber);
		}

		// last complete build number
		boolean isBuilding = getIsBuilding(moduleSet);
		HudsonModelRun lastCompletedHudsonRun;
		if (isBuilding) {
			lastCompletedHudsonRun = moduleSet.getLastCompletedBuild();
		} else {
			lastCompletedHudsonRun = moduleSet.getLastBuild();
		}
		if (lastCompletedHudsonRun != null) {
			lastCompleteBuildNumber = lastCompletedHudsonRun.getNumber();
		}
		if (lastCompleteBuildNumber != UNBUILT_PROJECT) {
			lastCompletedHudsonBuild = hudsonBuildFinder.find(name, lastCompleteBuildNumber);
		}

		HudsonProject hudsonProject = new HudsonProject();
		hudsonProject.setBuilding(isBuilding);
		hudsonProject.setLastBuildNumber(lastCompleteBuildNumber);
		hudsonProject.setName(name);
		hudsonProject.setDescription(description);
		hudsonProject.setArtifactId(artifactId);
		hudsonProject.setBuildNumbers(getBuildNumbers(moduleSet));
		hudsonProject.setCompletedBuild(lastCompletedHudsonBuild);
		hudsonProject.setCurrentBuild(currentHudsonBuild);
		return hudsonProject;
	}

	private int[] getBuildNumbers(HudsonModelJob modelJob) {
		List<HudsonModelRun> builds = modelJob.getBuild();
		int[] buildNumbers = new int[builds.size()];
		for (int i = 0; i < builds.size(); i++) {
			buildNumbers[i] = builds.get(i).getNumber();
		}
		return buildNumbers;
	}

	private String artifactId(String name) {
		String artifactId = "";
		try {
			artifactId = hudsonRootModuleFinder.findArtifactId(name);
		} catch (ArtifactIdNotFoundException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("can't find the artifactId for project " + name
						+ " cause:" + e.getCause());
			}
		}
		return artifactId;
	}
	
	private boolean getIsBuilding(HudsonModelJob modelJob) {
		String color = modelJob.getColor().value();
		return color.endsWith("_anime");
	}

	@VisibleForTesting
	void setHudsonRootModuleFinder(HudsonRootModuleFinder hudsonRootModuleFinder) {
		this.hudsonRootModuleFinder = hudsonRootModuleFinder;
	}
}
