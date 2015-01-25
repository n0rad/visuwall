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

import static com.google.common.collect.ImmutableMap.of;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import fr.norad.jaxrs.client.server.resource.mapper.ErrorExceptionMapper;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class CxfConfig {
    protected static final String STATIC_CONTENT_PATTERN = "/(.*\\.(html|css|js|gif|png|jpg|jpeg|ico|eot|svg|ttf|woff))";

    @Bean(destroyMethod = "shutdown", name = "cxf")
    public SpringBus cxf() {
        return new SpringBus();
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new CXFServlet(), "/*");
        servletRegistrationBean.setInitParameters(of("static-resources-list", STATIC_CONTENT_PATTERN));
        return servletRegistrationBean;
    }

    @Bean
    public JacksonJsonProvider jacksonJsonProvider(ObjectMapper mapper) {
        return new JacksonJsonProvider(mapper);
    }

    @Bean
    @DependsOn("cxf")
    public JAXRSServerFactoryBeanAutoConfigurer serverFactoryBeanAutoConfigurer() {
        return new JAXRSServerFactoryBeanAutoConfigurer();
    }

    private class JAXRSServerFactoryBeanAutoConfigurer implements
            ApplicationListener<ApplicationContextEvent>,
            BeanPostProcessor {

        @Value("${cxf.payload.logging.limit:100000}")
        private int payloadLoggingLimit;

        private final List<Object> jaxrsResources = new ArrayList<>();

        private final List<Object> jaxrsProviders = new ArrayList<>();

        private Server jaxrsServer;

        private JAXRSServerFactoryBeanAutoConfigurer() {
//            jaxrsProviders.add(new ValidationExceptionMapper());
            jaxrsProviders.add(new ErrorExceptionMapper());
//            jaxrsProviders.add(    new LoggingFilter());

        }

        @Override
        public void onApplicationEvent(ApplicationContextEvent event) {
            if (event instanceof ContextRefreshedEvent) {
                startJAXRSServer();
            } else if (event instanceof ContextClosedEvent) {
                destroyJAXRSServer();
            }
        }

        private void startJAXRSServer() {
            JAXRSServerFactoryBean jaxrsServerFactoryBean = new JAXRSServerFactoryBean();
            jaxrsServerFactoryBean.setAddress("/");
            jaxrsServerFactoryBean.setStaticSubresourceResolution(true);
            jaxrsServerFactoryBean.setServiceBeans(jaxrsResources);
            jaxrsServerFactoryBean.setProviders(jaxrsProviders);
//            jaxrsServerFactoryBean.getInInterceptors().add(new LoggingInInterceptor(payloadLoggingLimit));
//            jaxrsServerFactoryBean.getOutInterceptors().add(new LoggingOutInterceptor(payloadLoggingLimit));
            jaxrsServer = jaxrsServerFactoryBean.create();
        }

        private void destroyJAXRSServer() {
            if (jaxrsServer != null) {
                jaxrsServer.destroy();
            }
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (AnnotationUtils.findAnnotation(bean.getClass(), Path.class) != null) {
                jaxrsResources.add(bean);
            } else if (scanMethodsForAnnotation(bean.getClass(), Path.class) != null) {
                jaxrsResources.add(bean);
            }
            Provider providerAnnotation = AnnotationUtils.findAnnotation(bean.getClass(), Provider.class);
            if (providerAnnotation != null) {
                jaxrsProviders.add(bean);
            }

            return bean;
        }

        private Path scanMethodsForAnnotation(Class<?> aClass, final Class<Path> annotationClass) {
            for (Method method : aClass.getMethods()) {
                Path found = AnnotationUtils.findAnnotation(method, annotationClass);
                if (found != null) {
                    return found;
                }
            }
            return null;
        }

    }

}
