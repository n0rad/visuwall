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
package fr.norad.visuwall.providers.teamcity;

import static org.junit.Assert.assertEquals;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.junit.Test;
import fr.norad.visuwall.providers.teamcity.resource.TeamCityBuilds;

public class TeamCityBuildsTest {

    @Test
    public void should_get_all_builds() throws Exception {
        JAXBContext context = JAXBContext.newInstance(TeamCityBuilds.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        TeamCityBuilds builds = (TeamCityBuilds) unmarshaller.unmarshal(this.getClass().getClassLoader()
                .getResource("app/rest/builds/locator=running:true.xml"));

        assertEquals(2, builds.getCount());
        assertEquals(2, builds.getBuilds().size());
    }

}
