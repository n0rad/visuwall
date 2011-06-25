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

package net.awired.visuwall.core.tools;

import java.util.List;

import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.plugin.bamboo.BambooConnection;
import net.awired.visuwall.plugin.hudson.HudsonConnection;
import net.awired.visuwall.plugin.jenkins.JenkinsConnection;
import net.awired.visuwall.plugin.teamcity.TeamCityConnection;

public class Conswall {

    private BasicCapability capability;
    private List<String> projectNames;
    private List<SoftwareProjectId> softwareProjectIds;
    private BuildCapability buildCapability;

    public Conswall(BasicCapability capability) {
        this.capability = capability;
        if (capability instanceof BuildCapability) {
            buildCapability = (BuildCapability) capability;
        }
        projectNames = capability.findProjectNames();
        softwareProjectIds = capability.findAllSoftwareProjectIds();
    }

    public static void main(String[] args) {
        JenkinsConnection jenkinsConnection = new JenkinsConnection();
        jenkinsConnection.connect("http://ci.visuwall.awired.net");

        HudsonConnection hudsonConnection = new HudsonConnection();
        hudsonConnection.connect("http://fluxx.fr.cr:8080/hudson");

        TeamCityConnection teamCityConnection = new TeamCityConnection();
        teamCityConnection.connect("http://teamcity.jetbrains.com");

        BambooConnection bambooConnection = new BambooConnection();
        bambooConnection.connect("http://bamboo.visuwall.awired.net");

        BasicCapability[] plugins = { //
        jenkinsConnection, //
                hudsonConnection, //
                teamCityConnection, //
                bambooConnection //
        };

        for (BasicCapability capability : plugins) {
            new Conswall(capability).print();
        }

    }

    private void print() {
        System.out.println("\n------------------------ " + capability.getClass().getName()
                + " ------------------------");
        // printProjectNames();
        // printProjectIds();
        // printNames();
        // printDescriptions();
        // printMavenIds();
        if (buildCapability != null) {
            printLastBuildDates();
        }
    }

    private void printProjectNames() {
        System.out.println("\nproject names:");
        System.out.println("--------------");
        for (String projectName : projectNames) {
            System.out.println(projectName);
        }
    }

    private void printProjectIds() {
        System.out.println("\nsoftware project ids:");
        System.out.println("---------------------");
        for (SoftwareProjectId softwareProjectId : softwareProjectIds) {
            System.out.println(softwareProjectId);
        }
    }

    private void printDescriptions() {
        System.out.println("\ndescriptions:");
        System.out.println("--------------");
        for (SoftwareProjectId softwareProjectId : softwareProjectIds) {
            String description = null;
            try {
                description = capability.getDescription(softwareProjectId);
            } catch (ProjectNotFoundException e) {
                description = e.getMessage();
            }
            System.out.println(softwareProjectId + ": " + description);
        }
    }

    private void printNames() {
        System.out.println("\nnames:");
        System.out.println("-------");
        for (SoftwareProjectId softwareProjectId : softwareProjectIds) {
            String name = null;
            try {
                name = capability.getName(softwareProjectId);
            } catch (ProjectNotFoundException e) {
                name = e.getMessage();
            }
            System.out.println(softwareProjectId + ": " + name);
        }
    }

    private void printMavenIds() {
        System.out.println("\nmaven ids:");
        System.out.println("-----------");
        for (SoftwareProjectId softwareProjectId : softwareProjectIds) {
            String mavenId = null;
            try {
                mavenId = capability.getMavenId(softwareProjectId);
            } catch (Exception e) {
                mavenId = e.getMessage();
            }
            System.out.println(softwareProjectId + ": " + mavenId);
        }
    }

    private void printLastBuildDates() {
        System.out.println("\nlast build dates:");
        System.out.println("------------------");
        for (SoftwareProjectId softwareProjectId : softwareProjectIds) {
            String buildTimeInfo = null;
            try {
                int buildNumber = buildCapability.getLastBuildNumber(softwareProjectId);
                BuildTime buildTime = buildCapability.getBuildTime(softwareProjectId, buildNumber);
                buildTimeInfo = buildTime.toString();
            } catch (Exception e) {
                buildTimeInfo = e.getMessage();
            }
            System.out.println(softwareProjectId + ": " + buildTimeInfo);
        }
    }

}
