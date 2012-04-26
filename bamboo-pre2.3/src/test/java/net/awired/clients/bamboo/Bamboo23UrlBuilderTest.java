package net.awired.clients.bamboo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Bamboo23UrlBuilderTest {

    private Bamboo23UrlBuilder urlBuilder = new Bamboo23UrlBuilder("http://localhost:8085");

    @Test
    public void should_create_login_url() {
        assertEquals("http://localhost:8085/api/rest/login.action?username=admin&password=password",
                urlBuilder.getLoginUrl("admin", "password"));
    }

    @Test
    public void should_create_build_names_url() {
        assertEquals("http://localhost:8085/api/rest/listBuildNames.action?auth=3kBU2frLrG",
                urlBuilder.getBuildNamesUrl("3kBU2frLrG"));
    }

}
