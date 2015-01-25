/**
 *
 *     Copyright (C) norad.fr
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package fr.norad.visuwall.it.core;

import static com.google.common.collect.ImmutableMap.of;
import static fr.norad.jaxrs.client.server.rest.RestBuilder.rest;
import static fr.norad.visuwall.application.config.RootConfig.jacksonJsonProvider;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import javax.annotation.PostConstruct;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import fr.norad.jaxrs.client.server.rest.RestBuilder;
import fr.norad.jaxrs.client.server.rest.RestSession;
import fr.norad.visuwall.application.config.RootConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RootConfig.class)
@IntegrationTest({
        "server.port:0",
        "server.address:localhost",
        "server.servletPath:/",
        "visuwall.home:",
        "cassandra.cql3.hosts:localhost",
        "cassandra.cql3.port:9042",
})
@WebAppConfiguration
@ActiveProfiles("test")
//@DirtiesContext
public abstract class VisuwallSpringTest {

    @Value("${local.server.port}")
    private int port;

    //    @Autowired
//    EmbeddedWebApplicationContext server;
    private RestBuilder rest;
    private String serverUrl;
    private RestSession session;

    protected VisuwallSpringTest() {
        session = new RestSession();
        session.setContentType(APPLICATION_JSON_TYPE);
        session.setHeaders(of("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
    }
    //.asJson();

//    if(debugging()) {
//        HTTPClientPolicy clientPolicy = WebClient.getConfig(client).getHttpConduit().getClient();
//        clientPolicy.setConnectionTimeout(0);
//        clientPolicy.setReceiveTimeout(0);
//    }


    @PostConstruct
    public void postContruct() {
        rest = rest()
                .addProvider(jacksonJsonProvider());
        if (DebugUtils.isDebugging()) {
            rest.receiveTimeout(0L);
            rest.connectionTimeout(0L);
        }
        serverUrl = "http://localhost:" + port;
    }
//
//    public VersionResource versionAPI() {
//        return rest.buildClient(VersionResource.class, serverUrl, session);
//    }
//
//    public UsersResource usersAPI() {
//        return rest.buildClient(UsersResource.class, serverUrl, session);
//    }
//
//    public PublicThreadsResource publicAPI() {
//        return rest.buildClient(PublicThreadsResource.class, serverUrl, session);
//    }
//
//    public PrivateThreadsResource privateAPI() {
//        return rest.buildClient(PrivateThreadsResource.class, serverUrl, session);
//    }
//
//    public PublicCommand publicCommandAPI() {
//        return rest.buildClient(PublicCommand.class, serverUrl, session);
//    }
//
//    public PrivateCommand privateCommandAPI() {
//        return rest.buildClient(PrivateCommand.class, serverUrl, session);
//    }
//
//    public OffersResource offersAPI() {
//        return rest.buildClient(OffersResource.class, serverUrl, session);
//    }
//

}