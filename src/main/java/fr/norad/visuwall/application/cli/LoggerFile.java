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

import static fr.norad.visuwall.application.Visuwall.HOME_SYSTEM_PROPERTY_NAME;
import static fr.norad.visuwall.application.Visuwall.LOG_VISUWALL_NORAD;
import static fr.norad.visuwall.application.Visuwall.LOG_VISUWALL_ROOT;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class LoggerFile {
    private static final String LOG_HOME_CONFIG_FILENAME = "logback.xml";
    private static final String LOG_INTERNAL_CONFIG_TEMPLATE = "/visuwall-logback.xml";
    private static final String LOG_CONF_FILE_PATH_KEY = "logback.configurationFile";

    private final File logbackConf;
    private final File home;
    private LogLevel level;
    private LogLevel rootLevel;


    LoggerFile(File home, LogLevel level, LogLevel rootLevel) {
        this.home = home;
        this.level = level;
        this.rootLevel = rootLevel;
        logbackConf = new File(this.home, LOG_HOME_CONFIG_FILENAME);
    }

    public void prepare() {
        System.setProperty("logging.config", "file:" + logbackConf.getAbsolutePath()); // spring boot specific
        System.setProperty(HOME_SYSTEM_PROPERTY_NAME, home.getAbsolutePath());
        System.setProperty(LOG_CONF_FILE_PATH_KEY, logbackConf.getAbsolutePath());

        System.setProperty(LOG_VISUWALL_NORAD, level.toString());
        System.setProperty(LOG_VISUWALL_ROOT, level.toString());
        if (rootLevel != null) {
            System.setProperty(LOG_VISUWALL_ROOT, rootLevel.toString());
        }

        if (!logbackConf.exists()) {
            logbackConf.getParentFile().mkdirs();
            copyLogbackFile();
        }
    }

    private void copyLogbackFile() {
        InputStream from = null;
        OutputStream to = null;
        try {
            from = getClass().getResourceAsStream(LOG_INTERNAL_CONFIG_TEMPLATE);
            to = new FileOutputStream(logbackConf);
            byte[] buf = new byte[0x800];
            int bytesRead = from.read(buf);
            while (bytesRead > -1) {
                to.write(buf, 0, bytesRead);
                bytesRead = from.read(buf);
            }
        } catch (IOException e) {
            System.err.println("Cannot write logback configuration file to home folder : " + logbackConf);
            e.printStackTrace();
        } finally {
            if (from != null) {
                try {
                    from.close();
                } catch (IOException e) {
                }
            }
            if (to != null) {
                try {
                    to.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
