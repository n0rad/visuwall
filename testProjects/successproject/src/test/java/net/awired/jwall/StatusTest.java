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

package net.awired.jwall;

import java.util.Calendar;
import org.junit.Assert;
import org.junit.Test;

public class StatusTest {

    @Test
    public void should_test() throws Exception {
        String value = System.getenv("status");
        String valueProp = System.getProperty("status");
        System.out.println("##########" + value);
        System.out.println("##########" + valueProp);
        Thread.currentThread().sleep(30000L);
        //        if (value != null && !value.trim().equals("")) {
        //            System.out.println("status value :" + value);
        //            if ("fail".equals(value)) {
        //                Assert.fail("this is a fail project");
        //            } else if ("error".equals(value)) {
        //                throw new RuntimeException("This is a Error project");
        //            }
        //
        //        } 
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        if (minutes % 2 == 0) {
            Assert.fail("this is a failed project");
        }
    }
}
