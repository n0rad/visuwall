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

package net.awired.visuwall.core.business.domain;

import java.util.ArrayList;
import java.util.List;
import net.awired.ajsl.core.lang.reflect.ReflectTool;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.api.plugin.capability.MetricCapability;
import net.awired.visuwall.api.plugin.capability.TestCapability;
import net.awired.visuwall.api.plugin.capability.ViewCapability;

public enum CapabilitiesEnum {
    build(BuildCapability.class), //
    test(TestCapability.class), //
    metric(MetricCapability.class), //
    view(ViewCapability.class), //
    ;

    private final Class<? extends BasicCapability> capabilityClass;

    private CapabilitiesEnum(Class<? extends BasicCapability> capabilityClass) {
        this.capabilityClass = capabilityClass;
    }

    public Class<? extends BasicCapability> getCapabilityClass() {
        return capabilityClass;
    }

    public static List<CapabilitiesEnum> getCapabilitiesForClass(Class<? extends BasicCapability> pluginConnection) {
        List<CapabilitiesEnum> capabilitiesEnums = new ArrayList<CapabilitiesEnum>();
        for (CapabilitiesEnum capabilitiesEnum : values()) {
            //TODO create classImplements with a list of interfaces in params and a list of intefaces in result ?
            if (ReflectTool.classImplement(pluginConnection, capabilitiesEnum.getCapabilityClass())) {
                capabilitiesEnums.add(capabilitiesEnum);
            }
        }
        return capabilitiesEnums;
    }
}
