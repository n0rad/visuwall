package net.awired.clients.cruisecontrol;

import org.junit.Test;

public class CruiseControlIT {

    @Test
    public void test() throws Exception {
        CruiseControl control = CruiseControl.get();
        control.setCcHost("localhost");
        control.setCcHttpPort(8080);
        control.setCcRmiPort(1099);

        System.out.println(control.getAllProjects());
    }

}
