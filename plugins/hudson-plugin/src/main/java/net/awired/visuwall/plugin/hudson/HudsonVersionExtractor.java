package net.awired.visuwall.plugin.hudson;

import com.google.common.base.Preconditions;

/**
 * Must find hudson version like "Hudson ver. 1.407"
 */
public class HudsonVersionExtractor {

	private String content;

	public HudsonVersionExtractor(String content) {
		Preconditions.checkNotNull(content, "content is mandatory");
		this.content = content;
	}

	public String version() {
		String right = content.split("Hudson ver\\.")[1].trim();
		String version = right.split("<")[0];
		return version;
	}

}
