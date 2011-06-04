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

package net.awired.visuwall.hudsonclient.builder;

import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.Commiters;
import net.awired.visuwall.hudsonclient.HudsonJerseyClient;
import net.awired.visuwall.hudsonclient.generated.hudson.HudsonUser;

import com.google.common.base.Preconditions;

public class HudsonUserBuilder {

    private HudsonUrlBuilder hudsonUrlBuilder;
    private HudsonJerseyClient hudsonJerseyClient;

    public HudsonUserBuilder(HudsonUrlBuilder hudsonUrlBuilder, HudsonJerseyClient hudsonJerseyClient) {
        this.hudsonUrlBuilder = hudsonUrlBuilder;
        this.hudsonJerseyClient = hudsonJerseyClient;
    }

    public Commiters getCommiters(String[] commiterNames) {
        Preconditions.checkNotNull(commiterNames, "commiterNames is mandatory");
        Commiters commiters = new Commiters();
        for (String commiterName : commiterNames) {
            String url = hudsonUrlBuilder.getUserUrl(commiterName);
            HudsonUser hudsonUser = hudsonJerseyClient.getHudsonUser(url);
            Commiter commiter = new Commiter(hudsonUser.getId());
            commiter.setName(commiterName);
            commiter.setEmail(hudsonUser.getEmail());
            commiters.addCommiter(commiter);
        }
        return commiters;
    }

}
