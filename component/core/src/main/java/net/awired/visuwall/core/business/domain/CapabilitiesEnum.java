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
