package net.awired.clients.common;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Maven {

    DocumentLoader documentLoader = new DocumentLoader();

    public String findMavenIdFrom(String pomUrl) throws MavenIdNotFoundException {
        try {
            Document doc = documentLoader.loadFromUrl(pomUrl);
            String groupId = findValueInFirstLevel(doc, "groupId");
            String artifactId = findValueInFirstLevel(doc, "artifactId");
            if (groupId == null || artifactId == null) {
                throw new MavenIdNotFoundException("Can't find pom at " + pomUrl);
            }
            return groupId + ":" + artifactId;
        } catch (DocumentNotLoadedException e) {
            throw new MavenIdNotFoundException("Can't find pom at " + pomUrl, e);
        }
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
