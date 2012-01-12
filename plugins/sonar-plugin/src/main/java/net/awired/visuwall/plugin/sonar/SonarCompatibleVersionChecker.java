package net.awired.visuwall.plugin.sonar;

public class SonarCompatibleVersionChecker {

    private int minimumCompatibleVersionIntPart;
    private int minimumCompatibleVersionDecimalPart;

    public SonarCompatibleVersionChecker(Double sonarMinimumCompatibleVersion) {
        minimumCompatibleVersionIntPart = sonarMinimumCompatibleVersion.intValue();
        minimumCompatibleVersionDecimalPart = getDecimalPart(sonarMinimumCompatibleVersion);
    }

    public boolean versionIsCompatible(String version) {
        try {
            double versionAsFloat = Double.valueOf(version);
            return versionIsCompatible(versionAsFloat);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean versionIsCompatible(Double version) {
        int intPart = version.intValue();

        if (minimumCompatibleVersionIntPart < intPart) {
            return true;
        }

        if (minimumCompatibleVersionIntPart >= intPart) {
            int decimalPart = getDecimalPart(version);
            if (minimumCompatibleVersionDecimalPart > decimalPart) {
                return false;
            }
        }

        return true;
    }

    private Integer getDecimalPart(Double value) {
        if (!value.toString().contains(".")) {
            return 0;
        }
        return Integer.valueOf(value.toString().split("\\.")[1]);
    }

}
