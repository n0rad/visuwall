package net.awired.visuwall.server.application;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.Manifest;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component
public class VisuwallApplication implements ServletContextAware {

    public static final String HOME_KEY = "VISUWALL_HOME";

    private ServletContext context;

    protected String version = "Unknow Version";

    protected String home;

    //	@PostConstruct
    public void init() {
        try {
            version = findVersion(version);
            home = getHomeDir();

            System.out.println("######################################");
            System.out.println("version : " + version);
            System.out.println("home : " + home);
            System.out.println("######################################");
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public Properties visuwallProperties() {
        Properties prop = new Properties();
        prop.setProperty(HOME_KEY, home);
        return prop;
    }


    public String findVersion(String fallback) throws IOException {
        // runnnable war
        Enumeration<URL> manifests = VisuwallApplication.class.getClassLoader()
        .getResources("META-INF/MANIFEST.MF");
        while (manifests.hasMoreElements()) {
            URL res = manifests.nextElement();
            Manifest manifest = new Manifest(res.openStream());
            String v = manifest.getMainAttributes().getValue("VisuwallVersion");
            if (v != null)
                return v;
        }
        // tomcat like
        Manifest manifest = new Manifest(
                context.getResourceAsStream("META-INF/MANIFEST.MF"));
        String v = manifest.getMainAttributes().getValue("VisuwallVersion");
        if (v != null)
            return v;
        return fallback;
    }

    public static String getHomeDir() {
        // check JNDI for the home directory first
        try {
            InitialContext iniCtxt = new InitialContext();
            Context env = (Context) iniCtxt.lookup("java:comp/env");
            String value = (String) env.lookup(HOME_KEY);
            if (value != null && value.trim().length() > 0)
                return value.trim();

            value = (String) iniCtxt.lookup(HOME_KEY);
            if (value != null && value.trim().length() > 0)
                return value.trim();
        } catch (NamingException e) {
            // ignore
        }

        // finally check the system property
        String sysProp = System.getProperty(HOME_KEY);
        if (sysProp != null)
            return sysProp.trim();

        // look at the env var next
        try {
            String env = System.getenv(HOME_KEY);
            if (env != null)
                return env.trim();
        } catch (Throwable _) {
            // when this code runs on JDK1.4, this method fails
            // fall through to the next method
        }

        // if for some reason we can't put it within the webapp, use home
        // directory.
        return System.getProperty("user.home") + "/.visuwall";
    }

    public String getVersion() {
        return version;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
        init();
    }
}
