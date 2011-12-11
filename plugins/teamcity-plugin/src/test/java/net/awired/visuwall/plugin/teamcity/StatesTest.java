package net.awired.visuwall.plugin.teamcity;

import static org.junit.Assert.assertEquals;
import net.awired.visuwall.api.domain.BuildState;
import org.junit.Test;

public class StatesTest {

    @Test
    public void should_map_states_correctly() {
        assertEquals(BuildState.FAILURE, States.asVisuwallState("ERROR"));
        assertEquals(BuildState.SUCCESS, States.asVisuwallState("SUCCESS"));
        assertEquals(BuildState.UNSTABLE, States.asVisuwallState("FAILURE"));
    }

    @Test
    public void should_return_unknown_when_state_is_not_mappable() {
        assertEquals(BuildState.UNKNOWN, States.asVisuwallState("not_a_state"));
    }
}
