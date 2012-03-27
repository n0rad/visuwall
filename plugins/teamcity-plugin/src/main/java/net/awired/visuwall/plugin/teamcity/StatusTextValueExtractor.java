package net.awired.visuwall.plugin.teamcity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatusTextValueExtractor {

    public int extract(String statusText, String key) {
        if (!statusText.contains(key)) {
            return 0;
        }
        key = Pattern.quote(key);
        String regexp = "(.*)(" + key + ")(: )([0-9]*)(.*)";
        Pattern p = Pattern.compile(regexp);
        Matcher m = p.matcher(statusText);
        m.find();
        return Integer.valueOf(m.group(4));
    }
}
