package com.jsmadja.wall.projectwall.domain;

public class ProjectStatus {
	private String name;
	private boolean building;
	private int lastBuildId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isBuilding() {
		return building;
	}

	public void setBuilding(boolean building) {
		this.building = building;
	}

	public int getLastBuildId() {
		return lastBuildId;
	}

	public void setLastBuildId(int lastBuildId) {
		this.lastBuildId = lastBuildId;
	}
}
