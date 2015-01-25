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

import fr.norad.core.io.JarManifestUtils;
import lombok.Getter;

@Getter
public enum Visuwall {
    VISUWALL;

    public static final String VISUWALL_NAME = "Visuwall";
    public static final String HOME_SYSTEM_PROPERTY_NAME = "VISUWALL_HOME";
    public static final String LOG_VISUWALL_NORAD = "log.visuwall.norad";
    public static final String LOG_VISUWALL_ROOT = "log.visuwall.root";


    private static final String VERSION_MANIFEST_KEY = "VisuwallVersion";
    private static final String VERSION_UNKNOWN = "Unknown Version";

    private static final String COMMIT_ID_MANIFEST_KEY = "GitCommitId";
    private static final String COMMIT_TIME_MANIFEST_KEY = "gitCommitTime";

    private final String version;
    private final String commitId;
    private final String commitTime;

    {
        String manifestVersion = JarManifestUtils.getManifestAttribute(VERSION_MANIFEST_KEY);
        version = manifestVersion == null ? VERSION_UNKNOWN : "V" + manifestVersion;

        String manifestCommitId = JarManifestUtils.getManifestAttribute(COMMIT_ID_MANIFEST_KEY);
        commitId = manifestCommitId == null ? null : manifestCommitId.substring(0, 7);

        commitTime = JarManifestUtils.getManifestAttribute(COMMIT_TIME_MANIFEST_KEY);
    }

}
