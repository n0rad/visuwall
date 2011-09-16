package net.awired.clients.teamcity;

import net.awired.clients.teamcity.exception.TeamCityBuildNotFoundException;
import org.junit.Test;

public class TeamCityTest {

    @Test(expected = NullPointerException.class)
    public void cant_pass_null_as_parameter_in_constructor() {
        new TeamCity(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cant_pass_negative_number_to_find_build_method() throws TeamCityBuildNotFoundException {
        TeamCity teamcity = new TeamCity();
        teamcity.findBuild(-1);
    }

}
