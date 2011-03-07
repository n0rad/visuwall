package com.jsmadja.wall.projectwall.service;

import java.util.Date;
import java.util.List;

import com.jsmadja.wall.projectwall.domain.Build;
import com.jsmadja.wall.projectwall.domain.Project;

public interface Service {

    void setLogin(String login);

    void setPassword(String password);

    void setUrl(String url);

    void init();

    List<Project> findAllProjects();

    Project findProjectByName(String projectName) throws ProjectNotFoundException;

    Build findBuildByProjectNameAndBuildNumber(String projectName, int buildNumber) throws BuildNotFoundException;

    void populate(Project project) throws ProjectNotFoundException;

    Date getEstimatedFinishTime(Project project) throws ProjectNotFoundException;


}