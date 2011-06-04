package net.awired.visuwall.cli.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.awired.visuwall.cli.enumeration.LogLevelEnum;

public class ApplicationHelper {

	public static final String LOG_LVL_KEY = "VISUWALL_LOG";
	public static final String HOME_KEY = "VISUWALL_HOME";
	public static final String MANIFEST_VERSION_KEY = "VisuwallVersion";

	public static String findVersion(InputStream manifestIn) {
		// runnnable war
		try {
			Enumeration<URL> manifests = ApplicationHelper.class
					.getClassLoader().getResources("META-INF/MANIFEST.MF");
			while (manifests.hasMoreElements()) {
				URL res = manifests.nextElement();
				Manifest manifest = new Manifest(res.openStream());
				String versionManifest = manifest.getMainAttributes().getValue(
						MANIFEST_VERSION_KEY);
				if (versionManifest != null) {
					return versionManifest;
				}
			}
		} catch (IOException e) {
		}

		// tomcat like
		try {
			Manifest manifest = new Manifest(manifestIn);
			String versionManifest = manifest.getMainAttributes().getValue(
					MANIFEST_VERSION_KEY);
			if (versionManifest != null) {
				return versionManifest;
			}
		} catch (IOException e) {
		}
		return "Unknow Version";
	}
	
	public static LogLevelEnum findLogLvl() {
		String logLvl = System.getProperty(LOG_LVL_KEY);
		return LogLevelEnum.valueOf(logLvl);
	}

	public static String findHomeDir() {

		//TODO redirect to tmp or $TMP or check at start in cli if home is not writable  
		//		if (home == null) {
//			home = "/tmp";
//		}
		
		// check JNDI for the home directory first
		try {
			InitialContext iniCtxt = new InitialContext();
			Context env = (Context) iniCtxt.lookup("java:comp/env");
			String value = (String) env.lookup(HOME_KEY);
			if (value != null && value.trim().length() > 0)
				return value.trim();

			value = (String) iniCtxt.lookup(HOME_KEY);
			if (value != null && value.trim().length() > 0)
				return value.trim();
		} catch (NamingException e) {
			// ignore
		}

		// finally check the system property
		String sysProp = System.getProperty(HOME_KEY);
		if (sysProp != null)
			return sysProp.trim();

		// look at the env var next
		try {
			String env = System.getenv(HOME_KEY);
			if (env != null)
				return env.trim();
		} catch (Throwable _) {
			// when this code runs on JDK1.4, this method fails
			// fall through to the next method
		}

		// if for some reason we can't put it within the webapp, use home
		// directory.
		return System.getProperty("user.home") + "/.visuwall";
	}

}
