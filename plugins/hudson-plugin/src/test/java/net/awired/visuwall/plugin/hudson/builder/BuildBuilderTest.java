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

package net.awired.visuwall.plugin.hudson.builder;

import static org.junit.Assert.assertEquals;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;

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
