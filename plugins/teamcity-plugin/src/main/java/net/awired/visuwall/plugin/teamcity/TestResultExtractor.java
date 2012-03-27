package net.awired.visuwall.plugin.teamcity;

class TestResultExtractor {

    private StatusTextValueExtractor statusTextValueExtractor = new StatusTextValueExtractor();

    public int extractFailed(String statusText) {
        return statusTextValueExtractor.extract(statusText, "failed") + statusTextValueExtractor.extract(statusText, "errors");
    }

    public int extractPassed(String statusText) {
        return statusTextValueExtractor.extract(statusText, "passed");
    }

    public int extractIgnored(String statusText) {
        return statusTextValueExtractor.extract(statusText, "ignored");
    }

}
