package net.awired.visuwall.hudsonclient.builder;

import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.Commiters;
import net.awired.visuwall.hudsonclient.HudsonJerseyClient;
import net.awired.visuwall.hudsonclient.generated.hudson.HudsonUser;

import com.google.common.base.Preconditions;

public class HudsonUserBuilder {

    private HudsonUrlBuilder hudsonUrlBuilder;
    private HudsonJerseyClient hudsonJerseyClient;

    public HudsonUserBuilder(HudsonUrlBuilder hudsonUrlBuilder, HudsonJerseyClient hudsonJerseyClient) {
        this.hudsonUrlBuilder = hudsonUrlBuilder;
        this.hudsonJerseyClient = hudsonJerseyClient;
    }

    public Commiters getCommiters(String[] commiterNames) {
        Preconditions.checkNotNull(commiterNames, "commiterNames is mandatory");
        Commiters commiters = new Commiters();
        for (String commiterName : commiterNames) {
            String url = hudsonUrlBuilder.getUserUrl(commiterName);
            HudsonUser hudsonUser = hudsonJerseyClient.getHudsonUser(url);
            Commiter commiter = new Commiter(hudsonUser.getId());
            commiter.setName(commiterName);
            commiter.setEmail(hudsonUser.getEmail());
            commiters.addCommiter(commiter);
        }
        return commiters;
    }

}
