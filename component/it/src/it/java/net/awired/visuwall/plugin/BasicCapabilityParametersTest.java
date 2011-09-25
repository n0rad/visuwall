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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.plugin.capability.BasicCapability;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BasicCapabilityParametersTest {

    private BasicCapability connection;

    public BasicCapabilityParametersTest(Class<BasicCapability> clazz) throws InstantiationException,
            IllegalAccessException {
        this.connection = clazz.newInstance();
    }

    @Parameters
    public static Collection<Object[]> createData() {
        List<Class<? extends BasicCapability>> connections = new ArrayList<Class<? extends BasicCapability>>();
        //TODO put back
//        connections.add(JenkinsConnection.class);
//        connections.add(HudsonConnection.class);
        //        connections.add(TeamCityConnection.class);
        //        connections.add(BambooConnection.class);
//        connections.add(SonarConnection.class);

        List<Object[]> objects = new ArrayList<Object[]>();
        for (Class<? extends BasicCapability> clazz : connections) {
            objects.add(new Object[] { clazz });
        }
        return objects;
    }

    @Test(expected = NullPointerException.class)
    public void cant_pass_null_as_url_param() throws ConnectionException {
        connection.connect(null, null, null);
    }

    @Test
    public void can_pass_null_as_login_param() throws ConnectionException {
        connection.connect("url", null, null);
    }

    @Test
    public void can_pass_null_as_password_param() throws ConnectionException {
        connection.connect("url", "login", null);
    }

    @Test
    public void should_close_non_connected_connection() {
        connection.close();
    }

    @Test(expected = NullPointerException.class)
    public void cant_call_find_getDescription_with_null_param() throws Exception {
        connection.connect("url", null, null);
        connection.getDescription(null);
    }

}
