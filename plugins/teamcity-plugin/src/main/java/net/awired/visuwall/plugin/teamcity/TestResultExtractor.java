package net.awired.visuwall.plugin.teamcity;

class TestResultExtractor {

    public static int extractFailed(String statusText) {
        String key = "failed";
        return extract(statusText, key);
    }

    private static int extract(String statusText, String key) {
        String[] split = statusText.split(",");
        for (String s : split) {
            if (s.contains(key)) {
                String t = s.split(":")[1].trim();
                int indexOfBlank = t.indexOf(" ");
                if (indexOfBlank != -1) {
                    String substring = t.substring(0, indexOfBlank);
                    int parseInt = Integer.parseInt(substring);
                    return parseInt;
                } else {
                    return Integer.parseInt(t);
                }
            }
        }
        return 0;
    }

    public static int extractPassed(String statusText) {
        String key = "passed";
        return extract(statusText, key);
    }

    public static int extractIgnored(String statusText) {
        String key = "ignored";
        return extract(statusText, key);

    }

}
