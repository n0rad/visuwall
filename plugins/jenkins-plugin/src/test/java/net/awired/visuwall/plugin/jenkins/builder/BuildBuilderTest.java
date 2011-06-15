package net.awired.visuwall.plugin.jenkins.builder;

import static org.junit.Assert.assertEquals;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.plugin.jenkins.builder.BuildBuilder;

import org.junit.Test;

public class BuildBuilderTest {

	private BuildBuilder buildBuilder = new BuildBuilder();

	@Test
	public void should_not_fail_if_state_doesnt_exist() {
		HudsonBuild hudsonBuild = new HudsonBuild();
		hudsonBuild.setState("NOT_EXIST_BUILT");
		Build build = buildBuilder.createBuildFrom(hudsonBuild);
		assertEquals(State.UNKNOWN, build.getState());
	}

	@Test
	public void should_not_fail_if_state_is_null() {
		HudsonBuild hudsonBuild = new HudsonBuild();
		hudsonBuild.setState(null);
		Build build = buildBuilder.createBuildFrom(hudsonBuild);
		assertEquals(State.UNKNOWN, build.getState());
	}

	@Test
	public void should_set_valid_state() {
		HudsonBuild hudsonBuild = new HudsonBuild();
		hudsonBuild.setState(State.ABORTED.name());
		Build build = buildBuilder.createBuildFrom(hudsonBuild);
		assertEquals(State.ABORTED, build.getState());
	}

	@Test
	public void should_set_valid_state_case_insensitive() {
		HudsonBuild hudsonBuild = new HudsonBuild();
		hudsonBuild.setState(State.ABORTED.name().toLowerCase());
		Build build = buildBuilder.createBuildFrom(hudsonBuild);
		assertEquals(State.ABORTED, build.getState());
	}
}
