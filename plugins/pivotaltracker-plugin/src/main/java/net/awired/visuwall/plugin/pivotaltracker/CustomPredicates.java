package net.awired.visuwall.plugin.pivotaltracker;

import static net.awired.clients.pivotaltracker.resource.CurrentState.unstarted;
import static net.awired.clients.pivotaltracker.resource.StoryType.feature;
import net.awired.clients.pivotaltracker.resource.Story;

import com.google.common.base.Predicate;

public class CustomPredicates {

    public static Predicate<Story> isFeature = new Predicate<Story>() {
        @Override
        public boolean apply(Story story) {
            return story.getStoryType().equals(feature);
        }
    };

    public static Predicate<Story> isReady = new Predicate<Story>() {
        @Override
        public boolean apply(Story story) {
            return story.getCurrentState().equals(unstarted);
        }
    };

}
