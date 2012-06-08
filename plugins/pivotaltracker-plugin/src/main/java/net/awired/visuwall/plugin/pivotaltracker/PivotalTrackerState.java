package net.awired.visuwall.plugin.pivotaltracker;

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Collections2.filter;
import static net.awired.visuwall.api.domain.BuildState.FAILURE;
import static net.awired.visuwall.api.domain.BuildState.SUCCESS;
import static net.awired.visuwall.plugin.pivotaltracker.CustomPredicates.isFeature;
import static net.awired.visuwall.plugin.pivotaltracker.CustomPredicates.isReady;

import java.util.Map;

import net.awired.clients.pivotaltracker.resource.Stories;
import net.awired.visuwall.api.domain.BuildState;

public class PivotalTrackerState {

    public BuildState guessState(Map<BuildState, Integer> thresholds, Stories stories) {
        int countReadyFeatures = filter(stories.all(), and(isFeature, isReady)).size();
        int expectedMinimumReadyFeaturesCount = thresholds.get(SUCCESS);
        if (expectedMinimumReadyFeaturesCount <= countReadyFeatures) {
            return SUCCESS;
        }
        return FAILURE;
    }
}
