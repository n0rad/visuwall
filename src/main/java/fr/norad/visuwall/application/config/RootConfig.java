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
package fr.norad.visuwall.application.config;

import static fr.norad.visuwall.application.Visuwall.HOME_SYSTEM_PROPERTY_NAME;
import static fr.norad.visuwall.application.Visuwall.VISUWALL;
import static fr.norad.visuwall.application.Visuwall.VISUWALL_NAME;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.LogManager;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.base.Strings;
import fr.norad.visuwall.application.VisuwallHome;

@Configuration
@ComponentScan(basePackages = "fr.norad.visuwall")
@EnableAutoConfiguration
public class RootConfig {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void init() {
        SLF4JBridgeHandler.install();
        LogManager.getLogManager().reset();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Locale.setDefault(Locale.UK);
    }

    @Bean
    public static ObjectMapper jsonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }

    public static JacksonJsonProvider jacksonJsonProvider() {
        return new JacksonJsonProvider(jsonObjectMapper());
    }

    @Bean
    public VisuwallHome home() throws IOException {
        String homeProperty = System.getProperty(HOME_SYSTEM_PROPERTY_NAME);
        if (Strings.isNullOrEmpty(homeProperty)) {
            File homeFile = Files.createTempDirectory(VISUWALL_NAME).toFile();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        FileUtils.deleteDirectory(homeFile);
                    } catch (IOException e) {
                    }
                }
            });
            homeProperty = homeFile.getAbsolutePath();
        }

        VisuwallHome home = new VisuwallHome(new File(homeProperty));
        showInfo(home);
        return home.run();
    }

    private void showInfo(VisuwallHome home) throws IOException {
        log.info("####################################");
        log.info("Visuwall version : " + VISUWALL.getVersion());
        log.info("Home folder is : " + home.toString());
        log.info("###################################");
    }

}
