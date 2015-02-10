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
package fr.norad.visuwall.providers.cruisecontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

public class CruiseControl {

    public String ccHost;
    public int ccRmiPort;
    public int ccHttpPort;
    private String buildResults = "cruisecontrol/buildresults";

    private static final class BuildStatus {
        public static final String IN_QUEUE = "in build queue";
        public static final String WAITING = "waiting for next time to build";
        public static final String BOOTSTRAPPING = "bootstrapping";
        public static final String CHECKING_FOR_MODS = "checking for modifications";
        public static final String NOW_BUILDING = "now building";
        public static final String MERGING_LOGS = "merging accumulated log files";
        public static final String PUBLISHING = "publishing build results";
    }

    private static CruiseControl instance = null;

    public static CruiseControl get() {
        if (instance == null) {
            instance = new CruiseControl();
        }
        return instance;
    }

    private CruiseControl() {
    }

    public String getStatus(String project) {
        JMXConnector connector = createConnector();
        MBeanServerConnection mbsc;
        String projectStatus = null;
        try {
            mbsc = connector.getMBeanServerConnection();
            ObjectName cruiseObjectName = ObjectName.getInstance("CruiseControl Project:name=" + project);
            projectStatus = (String) mbsc.getAttribute(cruiseObjectName, "Status");
            connector.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (MBeanException e) {
            e.printStackTrace();
        } catch (ReflectionException e) {
            e.printStackTrace();
        }

        return projectStatus;
    }

    public String getLastSuccessfulBuildLabel(String project) {
        String lastbuildTime = (String) getAttribute("CruiseControl Project:name=" + project, "LastSuccessfulBuild");
        // http://softbuild01.emea.apc.com:8082/cruisecontrol/buildresults/CPM_60_ComponentTestXP?log=log20090521205615L38214
        URL url;
        try {

            url = new URL("http://" + ccHost + ":" + ccHttpPort + "/" + buildResults + "/" + project + "?log="
                    + lastbuildTime);

            URLConnection urlConnection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains(lastbuildTime)) {
                    String buildLabel = inputLine.substring(inputLine.indexOf(lastbuildTime) + lastbuildTime.length()
                            + 1);
                    return buildLabel.substring(0, buildLabel.indexOf("\""));
                }
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getLabel(String project) {
        return (String) getAttribute("CruiseControl Project:name=" + project, "Label");
    }

    @SuppressWarnings("unchecked")
    public List<String> getAllProjects() {
        return (List<String>) getAttribute("CruiseControl Manager:id=unique", "Projects");
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getAllProjectStatus() {
        return (Map<String, String>) getAttribute("CruiseControl Manager:id=unique", "AllProjectsStatus");
    }

    public void runBuild(String project) throws IOException, MalformedObjectNameException, NullPointerException,
            InstanceNotFoundException, MBeanException, ReflectionException {
        JMXConnector connector = createConnector();
        MBeanServerConnection mbsc = connector.getMBeanServerConnection();
        ObjectName cruiseObjectName = ObjectName.getInstance("CruiseControl Project:name=" + project);
        mbsc.invoke(cruiseObjectName, "build", null, null);
        connector.close();
    }

    public void forceBuild(String project) {
        MBeanServerConnection connection = null;
        try {
            connection = createConnector().getMBeanServerConnection();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        ObjectName mbeanObj;
        try {
            mbeanObj = ObjectName.getInstance("CruiseControl Project:name=" + project);
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }

        String status;

        try {
            status = (String) connection.getAttribute(mbeanObj, "Status");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (!BuildStatus.WAITING.equals(status) && !BuildStatus.IN_QUEUE.equals(status)) {
            System.out.println("Not forcing build because current status is '" + status + "'");
            return;
        }
        System.out.println("Forcing build...");
        try {
            connection.invoke(mbeanObj, "build", new Object[0], new String[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("sent 'build' msg successfully");
    }

    private Object getAttribute(String mbeanObject, String attribute) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        SecurityManager securityManager = System.getSecurityManager();

        System.setSecurityManager(new MyRMISecurityManager());
        JMXConnector connector = createConnector();
        MBeanServerConnection mbsc;
        try {
            mbsc = connector.getMBeanServerConnection();
            return mbsc.getAttribute(ObjectName.getInstance(mbeanObject), attribute);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (MBeanException e) {
            e.printStackTrace();
        } catch (ReflectionException e) {
            e.printStackTrace();
        } catch (MalformedObjectNameException e1) {
            e1.printStackTrace();
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        } finally {
            try {
                connector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            System.setSecurityManager(securityManager);
        }
        return null;
    }

    private JMXConnector createConnector() {
        JMXServiceURL address = null;
        try {
            address = new JMXServiceURL("rmi", getCcHost(), getCcRmiPort(), "/jndi/jrmp");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HashMap<String, String> environment = new HashMap<String, String>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
        environment.put(Context.PROVIDER_URL, "rmi://" + getCcHost() + ":" + getCcRmiPort());
        try {
            return JMXConnectorFactory.connect(address, environment);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

    private String getCcHost() {
        return ccHost;
    }

    private int getCcRmiPort() {
        return ccRmiPort;
    }

    private int getCcHttpPort() {
        return ccHttpPort;
    }

    public void setCcHost(String ccHost) {
        this.ccHost = ccHost;
    }

    public void setCcRmiPort(int ccRmiPort) {
        this.ccRmiPort = ccRmiPort;
    }

    public void setCcHttpPort(int ccHttpPort) {
        this.ccHttpPort = ccHttpPort;
    }

    private class MyRMISecurityManager extends SecurityManager {
        public void checkPermission() {
        }

        public void checkPermission(Permission perm) {
        }

        public void checkPermission(Permission perm, Object context) {
        }
    }
}