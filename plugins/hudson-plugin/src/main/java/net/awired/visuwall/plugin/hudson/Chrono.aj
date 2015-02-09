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

package fr.norad.visuwall.plugin.hudson;

public aspect Chrono {

    Object around() : execution(public * fr.norad.visuwall.plugin.hudson.HudsonConnection.* (..)) {
        long start = System.currentTimeMillis();
        try {
            return proceed();
        } finally {
        	String prefix = "";
        	Object method = thisJoinPointStaticPart.getSignature();
            long end = System.currentTimeMillis();
            long duration = end - start;
            if (duration > 1100) {
            	prefix = "[SLOW QUERY] ";
            }
            if (duration > 2100) {
                prefix = "[VERY SLOW QUERY] ";
            }
            if (duration >= 1100) {
                System.err.print("Chronometer "+prefix+method+" "+duration+" ms\r\n");
            }
        }
    }
    
}
