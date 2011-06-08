package net.awired.visuwall.hudsonclient.domain;

import com.google.common.base.Objects;

public class HudsonTestResult {

	private int failCount;
	private int passCount;
	private int skipCount;
	private int totalCount;

	public int getFailCount() {
		return failCount;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}

	public int getPassCount() {
		return passCount;
	}

	public void setPassCount(int passCount) {
		this.passCount = passCount;
	}

	public int getSkipCount() {
		return skipCount;
	}

	public void setSkipCount(int skipCount) {
		this.skipCount = skipCount;
	}

	public int getTotalCount() {
		return passCount + skipCount + failCount;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this) //
		        .add("passCount", passCount) //
		        .add("skipCount", skipCount) //
		        .add("failCount", failCount) //
		        .add("totalCount", totalCount).toString();
	}

}
