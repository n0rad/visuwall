package net.awired.visuwall.core.application.common;

public class VersionManager {

    private static final java.util.regex.Pattern versionPattern = java.util.regex.Pattern
            .compile("^(\\d+(\\.\\d+(\\.\\d+)?)?)-");
    private static final String[] versionCompleters = new String[] { ".0.0", ".0" };

    public static String getOsgiVersionFromMavenVersion(String version) {
        // Maven uses a '-' to separate the version qualifier, while
        // OSGi uses a '.', so we need to convert the first '-' to a
        // '.' and fill in any missing minor or micro version
        // components if necessary.
        final java.util.regex.Matcher matcher = versionPattern.matcher(version);
        if (!matcher.lookingAt()) {
            return version;
        }

        // Leave extra space for worst-case additional insertion:
        final StringBuffer sb = new StringBuffer(version.length() + 4);
        sb.append(matcher.group(1));

        if (null == matcher.group(3)) {
            final int count = null != matcher.group(2) ? 2 : 1;
            sb.append(versionCompleters[count - 1]);
        }

        sb.append('.');
        sb.append(version.substring(matcher.end(), version.length()));
        return sb.toString();
    }
}
