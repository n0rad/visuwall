/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
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

package net.awired.visuwall.plugin;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.plugin.bamboo.BambooConnection;
import net.awired.visuwall.plugin.hudson.HudsonConnection;
import net.awired.visuwall.plugin.jenkins.JenkinsConnection;
import net.awired.visuwall.plugin.sonar.SonarConnection;
import net.awired.visuwall.plugin.teamcity.TeamCityConnection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MandatoryConnectionTest {

    private static final List<String> METHODS_NOT_TO_TEST = Arrays.asList("equals", "close", "connect", "hashCode",
            "isClosed");

    private String className;
    private Method method;
    private BasicCapability basicCapability;

    public MandatoryConnectionTest(BasicCapability basicCapability, String className, Method method) {
        this.basicCapability = basicCapability;
        this.className = className;
        this.method = method;
    }

    @Parameters
    public static Collection<Object[]> createData() throws InstantiationException, IllegalAccessException {
        List<Class<? extends BasicCapability>> connections = new ArrayList<Class<? extends BasicCapability>>();
        connections.add(JenkinsConnection.class);
        connections.add(HudsonConnection.class);
        connections.add(TeamCityConnection.class);
        connections.add(BambooConnection.class);
        connections.add(SonarConnection.class);

        List<Object[]> objects = new ArrayList<Object[]>();
        for (Class<? extends BasicCapability> clazz : connections) {
            addMethodsToTest(objects, clazz);
        }
        return objects;
    }

    private static void addMethodsToTest(List<Object[]> objects, Class<? extends BasicCapability> clazz)
            throws InstantiationException, IllegalAccessException {
        BasicCapability basicCapability = clazz.newInstance();
        String className = clazz.getName();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (isTestable(method)) {
                objects.add(new Object[] { basicCapability, className, method });
            }
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testMethod() throws Throwable {
        assertTrue(basicCapability.isClosed());
        String fullMethodName = className + "." + method.getName();
        try {
            Class<?>[] parameterTypes = method.getParameterTypes();
            int numParameters = parameterTypes.length;
            method.invoke(basicCapability, new Object[numParameters]);
        } catch (Exception e) {
            if (e.getCause() instanceof IllegalStateException) {
                throw e.getCause();
            }
            e.printStackTrace();
            fail(fullMethodName + " can't be invoke without a connection");
        }
        fail("You can't invoke " + fullMethodName + " without being connected");
    }

    private static boolean isTestable(Method method) {
        return method.getModifiers() == 1 && !METHODS_NOT_TO_TEST.contains(method.getName());
    }

}
