package net.awired.visuwall.plugin.jenkins;

import com.google.common.base.Preconditions;

/**
 * Must find jenkins version like "Jenkins ver. 1.407"
 */
public class JenkinsVersionExtractor {

	private String content;

	public JenkinsVersionExtractor(String content) {
		Preconditions.checkNotNull(content, "content is mandatory");
		this.content = content;
	}

	public String version() {
		String right = content.split("Jenkins ver\\.")[1].trim();
		String version = right.split("<")[0];
		return version;
	}

}
