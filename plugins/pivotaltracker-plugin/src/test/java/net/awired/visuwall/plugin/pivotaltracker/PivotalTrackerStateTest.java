package net.awired.visuwall.plugin.pivotaltracker;

import static net.awired.clients.pivotaltracker.resource.CurrentState.accepted;
import static net.awired.clients.pivotaltracker.resource.CurrentState.unstarted;
import static net.awired.clients.pivotaltracker.resource.StoryType.chore;
import static net.awired.clients.pivotaltracker.resource.StoryType.feature;
import static net.awired.visuwall.api.domain.BuildState.FAILURE;
import static net.awired.visuwall.api.domain.BuildState.SUCCESS;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import net.awired.clients.pivotaltracker.resource.Stories;
import net.awired.clients.pivotaltracker.resource.Story;
import net.awired.visuwall.api.domain.BuildState;

import org.junit.Before;
import org.junit.Test;

public class PivotalTrackerStateTest {

    private PivotalTrackerState pivotalTrackerState = new PivotalTrackerState();
    private Stories stories;
    Map<BuildState, Integer> thresholds;

    @Before
    public void init() {
        stories = new Stories();
        thresholds = new HashMap<BuildState, Integer>();
    }

    @Test
    public void should_be_failure_if_there_is_no_story() {
        thresholds.put(SUCCESS, 5);

        BuildState state = pivotalTrackerState.guessState(thresholds, stories);

        assertEquals(FAILURE, state);
    }

    @Test
    public void should_be_success_if_there_is_one_story_and_threshold_is_1() {
        stories.add(new Story(feature, unstarted));

        thresholds.put(SUCCESS, 1);

        BuildState state = pivotalTrackerState.guessState(thresholds, stories);

        assertEquals(SUCCESS, state);
    }

    @Test
    public void should_be_success_if_there_is_more_stories_than_expected_threshold() {
        stories.add(new Story(chore, accepted));
        stories.add(new Story(feature, accepted));
        stories.add(new Story(feature, unstarted));
        stories.add(new Story(feature, unstarted));

        thresholds.put(SUCCESS, 2);

        BuildState state = pivotalTrackerState.guessState(thresholds, stories);

        assertEquals(SUCCESS, state);
    }
}
