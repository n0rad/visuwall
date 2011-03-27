package net.awired.visuwall.server.application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VisuwallApplication {

	@Autowired
	ServletContext context;
	
	
	private String version = "Unknow Version";

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
}
