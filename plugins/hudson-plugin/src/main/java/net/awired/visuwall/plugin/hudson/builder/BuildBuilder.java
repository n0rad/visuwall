package net.awired.visuwall.plugin.hudson.builder;

import java.util.HashSet;
import java.util.Set;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonCommiter;
import net.awired.visuwall.hudsonclient.domain.HudsonTestResult;

import com.google.common.base.Preconditions;

public class BuildBuilder {

	private HudsonBuild hudsonBuild;
	private Build build;

	public Build createBuildFrom(HudsonBuild hudsonBuild) {
		Preconditions.checkNotNull(hudsonBuild, "hudsonBuild");
		this.hudsonBuild = hudsonBuild;
		build();
		return this.build;
	}

	private void build() {
		build = new Build();
		build.setDuration(hudsonBuild.getDuration());
		build.setStartTime(hudsonBuild.getStartTime());
		build.setBuildNumber(hudsonBuild.getBuildNumber());
		addUnitTestResults();
		addIntegrationTestResults();
		addCommiters();
		addState();
	}

	private void addState() {
		String hudsonBuildState = hudsonBuild.getState();
		build.setState(State.getStateByName(hudsonBuildState));
	}

	private void addCommiters() {
		Set<HudsonCommiter> hudsonCommiters = hudsonBuild.getCommiters();
		Set<Commiter> commiters = createCommiters(hudsonCommiters);
		build.setCommiters(commiters);
	}

	private void addIntegrationTestResults() {
		HudsonTestResult hudsonIntegrationTestResult = hudsonBuild.getIntegrationTestResult();
		if (hudsonIntegrationTestResult != null) {
			TestResult integrationTestResult = createTestResult(hudsonIntegrationTestResult);
			build.setIntegrationTestResult(integrationTestResult);
		}
	}

	private HudsonTestResult addUnitTestResults() {
		HudsonTestResult hudsonUnitTestResult = hudsonBuild.getUnitTestResult();
		if (hudsonUnitTestResult != null) {
			TestResult unitTestResult = createTestResult(hudsonUnitTestResult);
			build.setUnitTestResult(unitTestResult);
		}
		return hudsonUnitTestResult;
	}

	private Set<Commiter> createCommiters(Set<HudsonCommiter> hudsonCommiters) {
		Set<Commiter> commiters = new HashSet<Commiter>();
		for (HudsonCommiter hudsonCommiter : hudsonCommiters) {
			commiters.add(createCommiter(hudsonCommiter));
		}
		return commiters;
	}

	private Commiter createCommiter(HudsonCommiter hudsonCommiter) {
		Commiter commiter = new Commiter(hudsonCommiter.getId());
		commiter.setEmail(hudsonCommiter.getEmail());
		commiter.setName(hudsonCommiter.getName());
		return commiter;
	}

	private TestResult createTestResult(HudsonTestResult hudsonTestResult) {
		TestResult testResult = new TestResult();
		testResult.setFailCount(hudsonTestResult.getFailCount());
		testResult.setPassCount(hudsonTestResult.getPassCount());
		testResult.setSkipCount(hudsonTestResult.getSkipCount());
		return testResult;
	}

}
