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
package fr.norad.visuwall.providers.hudson.resource;

import javax.xml.bind.annotation.XmlEnumValue;

public enum Color {

    @XmlEnumValue("red")
    RED("red"), @XmlEnumValue("red_anime")
    RED_ANIME("red_anime"), @XmlEnumValue("yellow")
    YELLOW("yellow"), @XmlEnumValue("yellow_anime")
    YELLOW_ANIME("yellow_anime"), @XmlEnumValue("blue")
    BLUE("blue"), @XmlEnumValue("blue_anime")
    BLUE_ANIME("blue_anime"), @XmlEnumValue("grey")
    GREY("grey"), @XmlEnumValue("grey_anime")
    GREY_ANIME("grey_anime"), @XmlEnumValue("disabled")
    DISABLED("disabled"), @XmlEnumValue("disabled_anime")
    DISABLED_ANIME("disabled_anime"), @XmlEnumValue("aborted")
    ABORTED("aborted"), @XmlEnumValue("aborted_anime")
    ABORTED_ANIME("aborted_anime");
    private final String value;

    Color(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Color fromValue(String v) {
        for (Color c : Color.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
