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
package fr.norad.visuwall.core.application.enumeration;

import ch.qos.logback.classic.Level;

public enum LogLevelEnum {
    all(Level.ALL), //
    trace(Level.TRACE), //
    debug(Level.DEBUG), //
    info(Level.INFO), //
    warn(Level.WARN), //
    error(Level.ERROR), // 
    off(Level.OFF) //
    ;

    private final Level level;

    private LogLevelEnum(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }
}
