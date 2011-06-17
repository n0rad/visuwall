package net.awired.visuwall.api.plugin.tck;

import net.awired.visuwall.api.exception.ViewNotFoundException;

public interface ViewCapabilityTCK {

	void should_list_all_views();

	void should_list_all_project_in_a_view() throws ViewNotFoundException;

	void should_find_project_ids_by_names();

	void should_find_all_projects_of_views();
}
