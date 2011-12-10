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

package net.awired.visuwall.server.application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.ServletContextEvent;
import net.awired.visuwall.core.application.common.ApplicationHelper;
import org.fusesource.jansi.AnsiConsole;
import org.springframework.web.context.ContextLoaderListener;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class VisuwallContextLoaderListener extends ContextLoaderListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {

        AnsiConsole.systemInstall();
        String home = ApplicationHelper.findHomeDir();
        String version = getVersion(event);
        System.setProperty(ApplicationHelper.HOME_KEY, home);

        ApplicationHelper.changeLogLvl();
        updateDbVersion(home, version);

        System.out.println("######################################");
        System.out.println("version : " + version);
        System.out.println("home : " + System.getProperty(ApplicationHelper.HOME_KEY));
        System.out.println("######################################");

        super.contextInitialized(event);
    }

    private String getVersion(ServletContextEvent event) {
        InputStream in = event.getServletContext().getResourceAsStream("META-INF/MANIFEST.MF");
        String currentVersion = ApplicationHelper.findVersion(in);
        return currentVersion;
    }

    private void updateDbVersion(String home, String currentVersion) {
        File versionFile = new File(home + "/version");

        if (ApplicationHelper.UNKNOW_VERSION.equals(currentVersion)) {
            return;
        }

        try {
            List<String> lines = Files.readLines(versionFile, Charsets.UTF_8);
            if (lines.size() > 0 && !lines.get(0).equals(currentVersion)) {
                System.out.println("Version of DB is " + lines.get(0) + " but currently running " + currentVersion
                        + " DB will be dropped");
                Files.deleteRecursively(new File(home + "/db"));
            }
        } catch (IOException e) {
            // no file found will not do anything
        } finally {
            try {
                Files.write(currentVersion.getBytes(), versionFile);
            } catch (IOException e) {
                System.err.println("cannot write visuwall version to file" + versionFile);
            }
        }

    }
}
