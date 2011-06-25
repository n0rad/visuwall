package net.awired.visuwall.plugin.bamboo;

import static org.junit.Assert.assertEquals;
import net.awired.visuwall.api.domain.State;

import org.junit.Test;

public class StatesTest {

    @Test
    public void should_convert_as_visuwall_state() {
        assertEquals(State.SUCCESS, States.asVisuwallState("Successful"));
        assertEquals(State.FAILURE, States.asVisuwallState("Failed"));
        assertEquals(State.UNKNOWN, States.asVisuwallState("Not a valid bamboo state"));
        assertEquals(State.UNKNOWN, States.asVisuwallState(null));
    }

}
