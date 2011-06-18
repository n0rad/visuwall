package net.awired.visuwall.api.plugin.tck;

import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;

public interface BuildCapabilityTCK {

	void should_find_build_by_build_number() throws BuildNotFoundException, ProjectNotFoundException;

	void should_get_last_build_number() throws ProjectNotFoundException, BuildNotFoundException;

	void should_get_success_build_state() throws ProjectNotFoundException;

	void should_get_is_building() throws ProjectNotFoundException;

    void should_get_estimated_date() throws ProjectNotFoundException;
}
