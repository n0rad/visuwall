package net.awired.visuwall.plugin.teamcity;

import java.util.Comparator;

class BuildIdComparator implements Comparator<String> {
    @Override
    public int compare(String s1, String s2) {
        int comparison = 0;
        try {
            comparison = Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
        } catch (NumberFormatException e) {
        }
        return comparison;
    }
}
