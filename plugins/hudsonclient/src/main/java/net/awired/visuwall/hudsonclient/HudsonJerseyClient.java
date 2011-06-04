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

package net.awired.visuwall.hudsonclient;

import net.awired.visuwall.hudsonclient.generated.hudson.hudsonmodel.HudsonModelHudson;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonMavenMavenModuleSet;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmodulesetbuild.HudsonMavenMavenModuleSetBuild;
import net.awired.visuwall.hudsonclient.generated.hudson.surefireaggregatedreport.HudsonMavenReportersSurefireAggregatedReport;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class HudsonJerseyClient {

    private Client client;

    public HudsonJerseyClient(Client client) {
        Preconditions.checkNotNull(client, "client is mandatory");
        this.client = client;
    }

    public HudsonJerseyClient() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getClasses();
        client = Client.create(clientConfig);
    }

    public HudsonMavenReportersSurefireAggregatedReport getSurefireReport(String url) {
        checkUrl(url);
        WebResource testResultResource = client.resource(url);
        HudsonMavenReportersSurefireAggregatedReport surefireReport = testResultResource
                .get(HudsonMavenReportersSurefireAggregatedReport.class);
        return surefireReport;
    }

    public HudsonMavenMavenModuleSetBuild getModuleSetBuild(String url) {
        checkUrl(url);
        WebResource jobResource = client.resource(url);
        HudsonMavenMavenModuleSetBuild setBuild = jobResource.get(HudsonMavenMavenModuleSetBuild.class);
        return setBuild;
    }

    public HudsonMavenMavenModuleSet getModuleSet(String url) throws UniformInterfaceException {
        checkUrl(url);
        WebResource projectResource = client.resource(url);
        HudsonMavenMavenModuleSet moduleSet = projectResource.get(HudsonMavenMavenModuleSet.class);
        return moduleSet;
    }

    public HudsonModelHudson getHudsonJobs(String url) {
        checkUrl(url);
        WebResource hudsonResource = client.resource(url);
        HudsonModelHudson hudson = hudsonResource.get(HudsonModelHudson.class);
        return hudson;
    }

    private void checkUrl(String url) {
        Preconditions.checkNotNull(url, "url is mandatory");
    }

}
