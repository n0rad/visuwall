/**
 *
 *     Copyright (C) norad.fr
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
package fr.norad.visuwall.api.plugin.tck;

import fr.norad.visuwall.api.exception.ViewNotFoundException;

public interface ViewCapabilityTCK {

    // findProjectNamesByView
    void should_list_all_project_in_a_view() throws ViewNotFoundException;

    // findProjectNamesByView
    void should_find_all_projects_of_views() throws ViewNotFoundException;

    // findViews
    void should_list_all_views();
}
