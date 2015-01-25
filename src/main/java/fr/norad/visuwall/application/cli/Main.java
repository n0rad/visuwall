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
package fr.norad.visuwall.application.cli;

import static fr.norad.visuwall.application.Visuwall.VISUWALL;
import static fr.norad.visuwall.application.Visuwall.VISUWALL_NAME;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import fr.norad.visuwall.application.config.RootConfig;

public class Main {

    public static void main(String[] args) {
        try {
            new Main().run(args);
        } catch (Throwable e) {
            LoggerFactory.getLogger(Main.class).error("Uncaught error : ", e);
            e.printStackTrace();
            System.exit(3);
        }
    }

    public void run(String[] cliArgs) {
        ArgumentManager args = processArgs(cliArgs);
        displayFile(args);
        displayVersion(args);
        clearDatabase(args);
        asService(args);
        new LoggerFile(args.homeFolder.getParamOneValue(),
                args.logLevel.getParamOneValue(),
                args.logRootLevel.getParamOneValue()).prepare();
        start(args);
    }

    /////////////////////////////////////////////////////////////

    private void start(ArgumentManager args) {
        new SpringApplicationBuilder(RootConfig.class)
                .showBanner(false)
                .run(
                        "--server.address=0.0.0.0",
                        "--server.port=" + args.serverPort.getParamOneValue(),
                        "--server.servletPath=" + args.serverServletPath.getParamOneValue());
    }

    private ArgumentManager processArgs(String[] cliArgs) {
        ArgumentManager args = new ArgumentManager();
        if (!(args.parseWithSuccess(cliArgs))) {
            System.exit(2);
        }
        return args;
    }

    private void clearDatabase(ArgumentManager args) {
        if (args.clearDatabase.isSet()) {
            //TODO needs to be implemented
        }
    }

    private void asService(ArgumentManager args) {
        if (args.startAsService.isSet()) {
            System.out.close();
            System.err.close();
        }
    }

    private void displayVersion(ArgumentManager args) {
        if (args.version.isSet()) {
            System.out.println(VISUWALL_NAME);
            System.out.println("Version : " + VISUWALL.getVersion());
            if (VISUWALL.getCommitId() != null) {
                System.out.println("Last commit id : " + VISUWALL.getCommitId() + " at " + VISUWALL.getCommitTime());
            }
            System.exit(0);
        }
    }

    private void displayFile(ArgumentManager args) {
        if (args.displayFile.isSet()) {
            args.displayFile.getParamOneValue().display();
            System.exit(0);
        }
    }

}
