package net.awired.visuwall.plugin.continuum;

import static net.awired.visuwall.api.domain.BuildState.FAILURE;
import static net.awired.visuwall.api.domain.BuildState.SUCCESS;
import static net.awired.visuwall.api.domain.BuildState.UNKNOWN;

import java.util.HashMap;
import java.util.Map;

import net.awired.visuwall.api.domain.BuildState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class States {

    private static final Logger LOG = LoggerFactory.getLogger(States.class);

    private static final Map<Integer, BuildState> STATE_MAPPING = new HashMap<Integer, BuildState>();

    static {
        STATE_MAPPING.put(2, SUCCESS);
        STATE_MAPPING.put(3, FAILURE);
    }

    public static final BuildState asVisuwallState(int continuumState) {
        BuildState state = STATE_MAPPING.get(continuumState);
        if (state == null) {
            state = UNKNOWN;
            LOG.warn(continuumState + " is not available in Continuum plugin. Please report it to Visuwall dev team.");
        }
        return state;
    }

}
