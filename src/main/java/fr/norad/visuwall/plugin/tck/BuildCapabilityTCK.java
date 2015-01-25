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
package fr.norad.visuwall.plugin.tck;

public interface BuildCapabilityTCK {

    // getBuildNumbers
    void should_get_build_numbers() throws Exception;

    // getEstimatedFinishTime
    void should_get_estimated_date() throws Exception;

    // getLastBuildNumber
    void should_get_last_build_number() throws Exception;

    // getLastBuildState
    void should_get_build_state() throws Exception;

    // isBuilding
    void should_get_is_building() throws Exception;

    // getBuildTime
    void should_get_build_time() throws Exception;

    // getBuildCommiters
    void should_get_commiters() throws Exception;
}
