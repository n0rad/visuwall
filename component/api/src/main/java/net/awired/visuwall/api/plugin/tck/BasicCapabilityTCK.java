package net.awired.visuwall.api.plugin.tck;

import net.awired.visuwall.api.exception.ProjectNotFoundException;

public interface BasicCapabilityTCK {

	void should_find_all_projects_ids();

	void should_find_project_ids_by_names();

	void should_contain_project();

	void should_not_contain_project();

	void should_find_all_project_names();

	void should_find_a_project() throws ProjectNotFoundException;

	void should_get_disable_project() throws ProjectNotFoundException;
}
