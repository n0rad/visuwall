package net.awired.visuwall.plugin.jenkins;

import net.awired.visuwall.api.domain.SoftwareId;

public class JenkinsVersionPage {

    private String content;

    public JenkinsVersionPage(String content) {
        this.content = content;
    }

    public SoftwareId createSoftwareId() {
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName("Jenkins");
        String strVersion = getVersion(content);
        softwareId.setVersion(strVersion);
        addWarnings(softwareId, strVersion);
        return softwareId;
    }

    private void addWarnings(SoftwareId softwareInfo, String strVersion) {
        double version = Double.parseDouble(strVersion);
        if (version < 1.405) {
            addWarningForVersionBefore1405(softwareInfo);
        }
    }

    private void addWarningForVersionBefore1405(SoftwareId softwareInfo) {
        softwareInfo.setWarnings("This jenkins version has a bug with git project. Git project wont be display.");
    }

    private String getVersion(String xml) {
        return new JenkinsVersionExtractor(xml).version();
    }

    public boolean isJenkinsApiPage() {
        return content.contains("Remote API [Jenkins]");
    }

}
