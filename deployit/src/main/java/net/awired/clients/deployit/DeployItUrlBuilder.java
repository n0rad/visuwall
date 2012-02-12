package net.awired.clients.deployit;


public class DeployItUrlBuilder {

    private String deployItUrl;
    private static final String API_URI = "/deployit/";

    // private static final Logger LOG = LoggerFactory.getLogger(DeployItUrlBuilder.class);

    public DeployItUrlBuilder(String deployItUrl) {
        this.deployItUrl = deployItUrl;
    }

    public String getArchivedTasksUrl() {
        return deployItUrl + API_URI + "export/tasks";
    }

}
