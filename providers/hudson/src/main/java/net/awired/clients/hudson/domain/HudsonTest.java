package net.awired.clients.hudson.domain;

public class HudsonTest {

    private String[] suffixes = { "IntegrationTest", "ITest", "IT" };

    private String className;
    private String status;

    public HudsonTest() {
    }

    public HudsonTest(String className, String status) {
        this.className = className;
        this.status = status;
    }

    @Override
    public String toString() {
        return className + ":" + status;
    }

    public boolean isIntegrationTest() {
        if (className.contains(".it."))
            return true;
        for (String suffix : suffixes)
            if (className.endsWith(suffix))
                return true;
        return false;
    }

    public boolean isUnitTest() {
        return !isIntegrationTest();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
