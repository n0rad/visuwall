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

import static fr.norad.visuwall.application.Visuwall.VISUWALL_NAME;
import static fr.norad.visuwall.application.VisuwallHome.defaultHomeDirectory;
import java.io.File;
import fr.norad.typed.command.line.parser.argument.CliArgumentManager;
import fr.norad.typed.command.line.parser.argument.args.CliNoParamArgument;
import fr.norad.typed.command.line.parser.argument.args.CliOneParamArgument;
import fr.norad.typed.command.line.parser.param.CliParamEnum;
import fr.norad.typed.command.line.parser.param.CliParamFile;
import fr.norad.typed.command.line.parser.param.CliParamInt;
import fr.norad.typed.command.line.parser.param.CliParamString;

class ArgumentManager extends CliArgumentManager {

    public final CliOneParamArgument<Integer> serverPort;
    public final CliOneParamArgument<String> serverServletPath;

    public final CliNoParamArgument startAsService;
    public final CliOneParamArgument<File> homeFolder;
    public final CliOneParamArgument<LogLevel> logLevel;
    public final CliOneParamArgument<LogLevel> logRootLevel;

    // without start
    public final CliNoParamArgument version;
    public final CliNoParamArgument clearDatabase;
    public final CliOneParamArgument<InfoFile> displayFile;

    public ArgumentManager() {
        super(VISUWALL_NAME);
        getUsageDisplayer().setUsageShort(true);

        startAsService = addArg(asService('s'));
        displayFile = addArg(displayFile('d'));
        version = addArg(version('v'));
        clearDatabase = addArg(clearDatabase('C'));
        serverPort = addArg(port('p'));
        serverServletPath = addArg(contactPath('c'));
        homeFolder = addArg(homeFolder('r'));
        logLevel = addArg(logLevel('l'));
        logRootLevel = addArg(rootLogLevel('L'));
    }

    ////////////////////////////////////////////////////////////////////////////////////

    private CliNoParamArgument clearDatabase(char c) {
        CliNoParamArgument startAsService = new CliNoParamArgument(c);
        startAsService.setName("cleardb");
        startAsService.setDescription("Clear database");
        return startAsService;
    }

    private CliNoParamArgument asService(char c) {
        CliNoParamArgument startAsService = new CliNoParamArgument(c);
        startAsService.setName("service");
        startAsService.setDescription("Start " + VISUWALL_NAME + " as a service");
        return startAsService;
    }

    private CliOneParamArgument<LogLevel> logLevel(char c) {
        CliOneParamArgument<LogLevel> logLevel = new CliOneParamArgument<>(c, new CliParamEnum<>("level", LogLevel.class));
        logLevel.setDescription("Change log level");
        logLevel.setParamOneDefValue(LogLevel.info);
        logLevel.setName("level");
        return logLevel;
    }

    private CliOneParamArgument<LogLevel> rootLogLevel(char c) {
        CliOneParamArgument<LogLevel> logLevel = new CliOneParamArgument<>(c, new CliParamEnum<>("level", LogLevel.class));
        logLevel.setDescription("Change log level for non-visuwall libs");
        logLevel.setParamOneDefValue(LogLevel.info);
        logLevel.setName("root-level");
        return logLevel;
    }

    private CliOneParamArgument<InfoFile> displayFile(char c) {
        CliOneParamArgument<InfoFile> displayFile = new CliOneParamArgument<>(c, new CliParamEnum<>("file", InfoFile.class));
        displayFile.setDescription("Display an information file and exit");
        displayFile.setName("display");
        return displayFile;
    }

    private CliOneParamArgument<File> homeFolder(char c) {
        CliOneParamArgument<File> homeFolder = new CliOneParamArgument<>(c, new CliParamFile("home"));
        homeFolder.setParamOneDefValue(defaultHomeDirectory());
        homeFolder.setName("home");
        homeFolder.setDescription(VISUWALL_NAME + " home folder");
        return homeFolder;
    }

    private CliOneParamArgument<Integer> port(char c) {
        CliParamInt portParam = new CliParamInt("port");
        portParam.setNegativable(false);
        portParam.setZeroable(false);
        CliOneParamArgument<Integer> port = new CliOneParamArgument<>(c, portParam);
        port.setParamOneDefValue(4242);
        port.setName("port");
        port.setDescription("Port to start " + VISUWALL_NAME);
        return port;
    }

    private CliOneParamArgument<String> contactPath(char c) {
        CliOneParamArgument<String> contextPath = new CliOneParamArgument<>(c, new CliParamString("contextPath") {
            @Override
            public String parse(String param) {
                if (param.charAt(0) != '/') {
                    return '/' + param;
                }
                return param;
            }
        });
        contextPath.setParamOneDefValue("/");
        contextPath.setName("contextpath");
        contextPath.setDescription("Context path to access " + VISUWALL_NAME);
        return contextPath;
    }

    private CliNoParamArgument version(char c) {
        CliNoParamArgument version = new CliNoParamArgument(c);
        version.setName("version");
        version.setDescription("Print " + VISUWALL_NAME + " information and exit");
        return version;
    }

}
