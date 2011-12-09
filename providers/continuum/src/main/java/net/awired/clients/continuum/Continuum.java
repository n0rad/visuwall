package net.awired.clients.continuum;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.continuum.xmlrpc.client.ContinuumXmlRpcClient;
import org.apache.maven.continuum.xmlrpc.project.ProjectGroupSummary;
import org.apache.maven.continuum.xmlrpc.project.ProjectSummary;

public class Continuum {

    private ContinuumXmlRpcClient client;

    public Continuum(String continuumUrl) {
        try {
            URL url = new URL(continuumUrl);
            client = new ContinuumXmlRpcClient(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public List<ProjectSummary> findAllProjects() throws Exception {
        List<ProjectGroupSummary> pgs = client.getAllProjectGroups();
        List<ProjectSummary> ps = new ArrayList<ProjectSummary>();
        for (ProjectGroupSummary projectGroupSummary : pgs) {
            ps.addAll(client.getProjects(projectGroupSummary.getId()));
        }
        return ps;
    }

    public ProjectSummary findProject(int projectId) throws Exception {
        return client.getProjectSummary(projectId);
    }

}
