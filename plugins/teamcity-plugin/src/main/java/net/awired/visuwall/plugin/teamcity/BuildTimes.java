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

package fr.norad.visuwall.plugin.teamcity;

import java.util.Date;
import fr.norad.visuwall.providers.teamcity.resource.TeamCityBuild;
import fr.norad.visuwall.api.domain.BuildTime;

class BuildTimes {

    private BuildTimes() {
    }

    static BuildTime createFrom(TeamCityBuild teamcityBuild) {
        BuildTime buildTime = new BuildTime();
        Date finishDate = DateAdapter.parseDate(teamcityBuild.getFinishDate());
        Date startDate = DateAdapter.parseDate(teamcityBuild.getStartDate());
        long duration = finishDate.getTime() - startDate.getTime();
        buildTime.setDuration(duration);
        buildTime.setStartTime(startDate);
        return buildTime;
    }
}
