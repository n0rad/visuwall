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
package fr.norad.visuwall.providers.common;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import fr.norad.visuwall.exception.MavenIdNotFoundException;

public class Maven {

    DocumentLoader documentLoader = new DocumentLoader();

    public String findMavenIdFrom(String pomUrl) throws MavenIdNotFoundException {
        try {
            Document document = documentLoader.loadFromUrl(pomUrl);
            return findMavenId(document);
        } catch (DocumentNotLoadedException e) {
            throw new MavenIdNotFoundException("Can't find pom at " + pomUrl, e);
        }
    }

    public String findMavenIdFromContent(String pomContent) throws MavenIdNotFoundException {
        try {
            Document document = documentLoader.loadFromContent(pomContent);
            return findMavenId(document);
        } catch (DocumentNotLoadedException e) {
            throw new MavenIdNotFoundException("Can't find maven id", e);
        }
    }

    private String findMavenId(Document doc) throws MavenIdNotFoundException {
        String groupId = findValueInFirstLevel(doc, "groupId");
        String artifactId = findValueInFirstLevel(doc, "artifactId");
        if (groupId == null || artifactId == null) {
            throw new MavenIdNotFoundException("Can't find maven id");
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
