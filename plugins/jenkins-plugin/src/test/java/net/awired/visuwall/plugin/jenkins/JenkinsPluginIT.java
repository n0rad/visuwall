package net.awired.visuwall.plugin.jenkins;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.domain.SoftwareProjectId;

import org.junit.Assert;
import org.junit.Test;

public class JenkinsPluginIT {

    @Test
    public void test() throws Exception {
        JenkinsPlugin plugin = new JenkinsPlugin();

        Map<String,String> properties = new HashMap<String, String>();
        properties.put("login", "jsmadja@xebia.fr");
        properties.put("password", "xedy4bsa");

        SoftwareId softwareId = plugin.getSoftwareId(new URL("https://jsmadja.ci.cloudbees.com"), properties);

        Assert.assertTrue(softwareId.isCompatible());
        System.err.println(softwareId.getVersion());


    }


}
