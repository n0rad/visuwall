package net.awired.visuwall.server.application;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component
public class VisuwallApplication implements ServletContextAware {

	
	private String version = "Unknow Version";

	private ServletContext context;
	
    /**
     * Figures out the version from the manifest.
     */
    private String findVersion(String fallback) throws IOException {
    	// runnnable war
        Enumeration<URL> manifests = VisuwallApplication.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
        while (manifests.hasMoreElements()) {
            URL res = (URL)manifests.nextElement();
            Manifest manifest = new Manifest(res.openStream());
            String v = manifest.getMainAttributes().getValue("VisuwallVersion");
            if(v!=null)
                return v;
        }
        // tomcat like
        Manifest manifest = new Manifest(context.getResourceAsStream("META-INF/MANIFEST.MF"));
        String v = manifest.getMainAttributes().getValue("VisuwallVersion");
        if(v!=null)
            return v;       
        return fallback;
    }
	
	@PostConstruct
	public void init() {
		try {
			version = findVersion(version);
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	public String getVersion() {
		return version;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.context = servletContext;
	}
}
