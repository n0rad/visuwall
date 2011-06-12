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

package net.awired.visuwall.cli;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;
import net.awired.visuwall.core.application.common.ApplicationHelper;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.io.Files;

public class Main {

    private final ArgumentManager argManager = new ArgumentManager(this);

    public static void main(String[] args) {
        new Main().run(args);
    }

    public void run(String[] args) {
        argManager.parse(args);

        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(argManager.logLevel.getParamOneValue().getLevel());

        if (argManager.displayFile.isSet()) {
            argManager.displayFile.getParamOneValue().display();
            System.exit(0);
        }

        if (argManager.info.isSet()) {
            printInfo();
            System.exit(0);
        }

        if (argManager.clearDb.isSet()) {
            cleanDB();
            System.exit(0);
        }
        runServer();
    }

    public void cleanDB() {
        String home = ApplicationHelper.findHomeDir();
        try {
            System.out.println("clearing database in home folder : " + home);
            Files.deleteRecursively(new File(home + "/db"));
        } catch (IOException e) {
        }
        System.exit(0);
    }

    public void printInfo() {
        System.out.println("Visuall version : " + ApplicationHelper.findVersion(null));
        System.out.println("Visuall home : " + ApplicationHelper.findHomeDir());
        // TODO list plugin founds
    }

    public void runServer() {
        final Server server = new Server();
        SocketConnector connector = new SocketConnector();

        // Set some timeout options to make debugging easier.
        connector.setMaxIdleTime(1000 * 60 * 60);
        connector.setSoLingerTime(-1);
        connector.setPort(argManager.portArg.getParamOneValue());
        server.setConnectors(new Connector[] { connector });

        WebAppContext context = new WebAppContext();
        context.setServer(server);
        context.setContextPath(argManager.contextPath.getParamOneValue());

        ProtectionDomain protectionDomain = Main.class.getProtectionDomain();
        URL location = protectionDomain.getCodeSource().getLocation();
        context.setDescriptor(location.toExternalForm() + "/WEB-INF/web.xml");
        context.setWar(location.toExternalForm());

        server.addHandler(context);
        try {
            server.start();
            server.setStopAtShutdown(true);
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }
}
