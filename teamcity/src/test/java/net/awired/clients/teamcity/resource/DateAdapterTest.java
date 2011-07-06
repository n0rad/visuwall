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

package net.awired.clients.teamcity.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Calendar;
import java.util.Date;
import org.junit.Test;

public class DateAdapterTest {

    @Test
    public void should_parse_date() {
        // 02/03/2011 17h19mn40 + 0300
        // equivaut a 
        // 02/03/2011 15h19mn40
        Date date = DateAdapter.parseDate("20110302T171940+0300");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        assertEquals(2011, cal.get(Calendar.YEAR));
        assertEquals(2, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(3, cal.get(Calendar.MONTH) + 1);
        assertTrue(15 == cal.get(Calendar.HOUR_OF_DAY) || 9 == cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(19, cal.get(Calendar.MINUTE));
        assertEquals(40, cal.get(Calendar.SECOND));
    }

    @Test(expected = NullPointerException.class)
    public void cant_pass_null_as_parameter() {
        DateAdapter.parseDate(null);
    }

}
