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

package net.awired.clients.teamcity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.common.base.Preconditions;

public class DateAdapter {

    private static final SimpleDateFormat TEAMCITY_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");

    private DateAdapter() {
    }

    public static Date parseDate(String dateToParse) {
        Preconditions.checkNotNull(dateToParse, "dateToParse is mandatory");
        try {
            return TEAMCITY_DATE_FORMAT.parse(dateToParse);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
