package net.awired.visuwall.plugin.teamcity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TestResultExtractor {

    public static int extractFailed(String statusText) {
        return extract(statusText, "failed") + extract(statusText, "errors");
    }

    public static int extractPassed(String statusText) {
        return extract(statusText, "passed");
    }

    public static int extractIgnored(String statusText) {
        return extract(statusText, "ignored");
    }

    private static int extract(String statusText, String key) {
        if (!statusText.contains(key)) {
            return 0;
        }
        String regexp = "(.*)(" + key + ")(: )([0-9]*)(.*)";
        Pattern p = Pattern.compile(regexp);
        Matcher m = p.matcher(statusText);
        m.find();
        return Integer.valueOf(m.group(4));
    }

}
