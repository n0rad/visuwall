package net.awired.clients.teamcity.exception;

public class TeamCityBuildTypeNotFoundException extends Exception {

    public TeamCityBuildTypeNotFoundException(String message, Throwable t) {
        super(message, t);
    }

}
