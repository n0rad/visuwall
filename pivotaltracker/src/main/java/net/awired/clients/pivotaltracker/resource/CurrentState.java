package net.awired.clients.pivotaltracker.resource;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(name = "current_state")
public enum CurrentState {
    accepted, unstarted, delivered, unscheduled, started
}
