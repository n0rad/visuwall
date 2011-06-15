package net.awired.visuwall.plugin.bamboo.builder;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.bambooclient.domain.BambooBuild;
import net.awired.visuwall.plugin.bamboo.States;

import com.google.common.base.Preconditions;

public class BuildBuilder {

	private Build build;
	private BambooBuild bambooBuild;

	public Build createFrom(BambooBuild bambooBuild) {
		Preconditions.checkNotNull(bambooBuild, "bambooBuild is mandatory");
		this.bambooBuild = bambooBuild;
		build = new Build();
		build();
		return build;
	}

	private void build() {
		build.setBuildNumber(bambooBuild.getBuildNumber());
		build.setDuration(bambooBuild.getDuration());
		build.setStartTime(bambooBuild.getStartTime());
		addState();
		addUnitTestResults();
	}

	private void addState() {
	    String bambooState = bambooBuild.getState();
		State visuwallState = States.asVisuwallState(bambooState);
		build.setState(visuwallState);
    }

	private void addUnitTestResults() {
	    TestResult unitTestResult = createUnitTestResult(bambooBuild);
		build.setUnitTestResult(unitTestResult);
    }

	private TestResult createUnitTestResult(BambooBuild bambooBuild) {
		TestResult unitTestResult = new TestResult();
		unitTestResult.setFailCount(bambooBuild.getFailCount());
		unitTestResult.setPassCount(bambooBuild.getPassCount());
		return unitTestResult;
	}

}
