package net.awired.visuwall.core.domain;

import javax.persistence.Embeddable;

import com.google.common.base.Objects;

@Embeddable
public class Software {

	private String className;
	private float version;

	public Software() {
	}

	public Software(String className, float version) {
		this.className = className;
		this.version = version;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this) //
				.add("className", className) //
				.add("version", version) //
				.toString();
	}

	// ///////////////////////////////////////////////////////////

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public float getVersion() {
		return version;
	}

	public void setVersion(float version) {
		this.version = version;
	}
}
