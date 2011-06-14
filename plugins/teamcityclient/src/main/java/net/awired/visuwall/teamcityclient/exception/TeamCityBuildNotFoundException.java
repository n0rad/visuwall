package net.awired.visuwall.teamcityclient.exception;

public class TeamCityBuildNotFoundException extends Exception {

	private static final long serialVersionUID = 3545017024044265453L;

	public TeamCityBuildNotFoundException(String msg, Throwable t) {
		super(msg, t);
	}

}
