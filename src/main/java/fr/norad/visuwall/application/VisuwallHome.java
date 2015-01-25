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
package fr.norad.visuwall.application;

import static fr.norad.visuwall.application.Visuwall.VISUWALL_NAME;
import java.io.Closeable;
import java.io.File;
import fr.norad.core.io.file.FileLocker;
import fr.norad.operating.system.specific.ApplicationHomeFactory;

public class VisuwallHome implements Closeable {
    private final File home;
    private final HomeLocker homeLocker;

    public VisuwallHome(File home) {
        this.home = home;
        homeLocker = new HomeLocker(home);
    }

    public VisuwallHome run() {
        if (!home.exists()) {
            home.mkdirs();
        }

        homeLocker.run();
        return this;
    }

    @Override
    public String toString() {
        return home.getAbsolutePath();
    }

    @Override
    public void close() {
        homeLocker.close();
    }

    public File getCassandraEmbeddedDirectory() {
        return new File(home, "cassandra");
    }

    ////////////////////////////////////////////////

    public static File defaultHomeDirectory() {
        return ApplicationHomeFactory.getApplicationHome().getFolder(VISUWALL_NAME);
    }

    static class HomeLocker implements Closeable {

        private static final String LOCK_FILENAME = "lock";

        private FileLocker lock;
        private final File home;

        public HomeLocker(File home) {
            this.home = home;
        }

        public synchronized void run() {
            lock = new FileLocker(new File(home, LOCK_FILENAME));
            if (lock.isLocked()) {
                throw new SecurityException("Apiboot home folder : " + home + " is locked. Another instance using this home is probably running");
            }
        }

        @Override
        public synchronized void close() {
            lock.close();
        }
    }

}
