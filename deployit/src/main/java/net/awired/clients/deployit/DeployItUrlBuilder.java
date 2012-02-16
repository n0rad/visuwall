package net.awired.clients.deployit;

public class DeployItUrlBuilder {

    private String deployItUrl;
    private static final String API_URI = "/deployit/";

    public DeployItUrlBuilder(String deployItUrl) {
        this.deployItUrl = deployItUrl;
    }

    public String getArchivedTasksUrl() {
        return deployItUrl + API_URI + "export/tasks";
    }

    public String getEnvironmentNamesUrl() {
        return query("typeName=udm.Environment");
    }

    public String getDeployedApplicationsNamesByEnvironment(String environmentName) {
        return query("parent=Environments/" + environmentName + "&typeName=udm.DeployedApplication");
    }

    private String query(String values) {
        return deployItUrl + API_URI + "query?" + values;
    }

}
