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

package net.awired.visuwall.hudsonclient.finder;

import net.awired.visuwall.hudsonclient.builder.HudsonUrlBuilder;
import net.awired.visuwall.hudsonclient.exception.ArtifactIdNotFoundException;
import net.awired.visuwall.hudsonclient.exception.DocumentNotLoadedException;
import net.awired.visuwall.hudsonclient.loader.DocumentLoader;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class HudsonRootModuleFinder {

    @VisibleForTesting
    HudsonUrlBuilder hudsonUrlBuilder;

    @VisibleForTesting
    public DocumentLoader documentLoader = new DocumentLoader();

    public HudsonRootModuleFinder(HudsonUrlBuilder hudsonUrlBuilder) {
        this.hudsonUrlBuilder = hudsonUrlBuilder;
    }

    public String findArtifactId(String jobName) throws ArtifactIdNotFoundException {
        Preconditions.checkNotNull(jobName, "jobName is mandatory");
        String pomUrl = hudsonUrlBuilder.getPomUrl(jobName);
        try {
            return createArtifactIdFrom(pomUrl);
        } catch (DocumentNotLoadedException e) {
            String logMessage = "Can't find artifactId, job : " + jobName + " at url :'" + pomUrl + "'";
            throw new ArtifactIdNotFoundException(logMessage, e);
        }
    }

    private String createArtifactIdFrom(String pomUrl) throws DocumentNotLoadedException {
        Document doc = documentLoader.loadFromUrl(pomUrl);
        String groupId = findValueInFirstLevel(doc, "groupId");
        String artifactId = findValueInFirstLevel(doc, "artifactId");
        if (groupId == null || artifactId == null) {
            return null;
        }
        return groupId + ":" + artifactId;
    }

    private String findValueInFirstLevel(Document doc, String tagName) {
        NodeList artifactIds = doc.getElementsByTagName(tagName);
        for (int i = 0; i < artifactIds.getLength(); i++) {
            Node artifactId = artifactIds.item(i);
            String parentName = artifactId.getParentNode().getNodeName();
            if (isFirstLevel(parentName)) {
                return artifactId.getTextContent();
            }
        }
        return null;
    }

    private boolean isFirstLevel(String parentName) {
        return "project".equals(parentName);
    }

}
