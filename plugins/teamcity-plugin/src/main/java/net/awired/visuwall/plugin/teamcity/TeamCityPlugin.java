package net.awired.visuwall.plugin.teamcity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import net.awired.visuwall.api.domain.PluginInfo;
import net.awired.visuwall.api.domain.SoftwareInfo;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;
import net.awired.visuwall.api.plugin.ConnectionPlugin;
import net.awired.visuwall.api.plugin.VisuwallPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

public class TeamCityPlugin implements VisuwallPlugin {

	private static final Logger LOG = LoggerFactory.getLogger(TeamCityPlugin.class);

	@Override
	public ConnectionPlugin getConnection(String url, Properties info) {
		return null;
	}

	@Override
	public PluginInfo getInfo() {
		PluginInfo pluginInfo = new PluginInfo();
		pluginInfo.setClassName(PluginInfo.class.getName());
		pluginInfo.setName("TeamCity plugin");
		pluginInfo.setVersion(1.0f);
		return pluginInfo;
	}

	@Override
	public SoftwareInfo getSoftwareInfo(URL url) throws IncompatibleSoftwareException {
		Preconditions.checkNotNull(url, "url is mandatory");
		String xml = getContent(url);
		if (isManageable(xml)) {
			try {
				return createSoftwareInfo(url);
			} catch (MalformedURLException e) {
				throw new IncompatibleSoftwareException("Url " + url + " is not compatible with TeamCity", e);
			}
		}
		throw new IncompatibleSoftwareException("Url " + url + " is not compatible with TeamCity");
	}

	private SoftwareInfo createSoftwareInfo(URL url) throws MalformedURLException {
		SoftwareInfo softwareInfo = new SoftwareInfo();
		softwareInfo.setPluginInfo(getInfo());
		softwareInfo.setName("TeamCity");
		String strVersion = getVersion(url);
		softwareInfo.setVersion(strVersion);
		return softwareInfo;
	}

	private String getVersion(URL url) throws MalformedURLException {
		URL versionUrl = new URL(url.toString() + "/app/rest/version");
		String xml = getContent(versionUrl);
		return new TeamCityVersionExtractor(xml).version();
	}

	private boolean isManageable(String xml) {
		return xml.contains("TeamCity");
	}

	private String getContent(URL url) {
		InputStream stream = null;
		try {
			url = new URL(url.toString() + "/app/rest/server");
			stream = url.openStream();
			byte[] content = ByteStreams.toByteArray(stream);
			String xml = new String(content);
			return xml;
		} catch (IOException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Can't get content of " + url, e);
			}
			return "";
		} finally {
			Closeables.closeQuietly(stream);
		}
	}

}
