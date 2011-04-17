package net.awired.visuwall.server.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectStatus;
import net.awired.visuwall.plugin.hudson.HudsonPlugin;
import net.awired.visuwall.plugin.sonar.SonarPlugin;
import net.awired.visuwall.server.domain.PluginHolder;
import net.awired.visuwall.server.domain.Software;
import net.awired.visuwall.server.domain.SoftwareAccess;
import net.awired.visuwall.server.domain.Wall;
import net.awired.visuwall.server.exception.NotCreatedException;
import net.awired.visuwall.server.exception.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

@Repository
@Transactional
public class WallService {

	private static final Logger LOG = LoggerFactory
			.getLogger(WallService.class);

	private final static int EVERY_FIVE_MINUTES = 5 * 60 * 1000;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private PluginService pluginService;

	private static Map<String, Wall> WALLS;

	@PostConstruct
	void init() throws NotCreatedException {
		if (WALLS == null) {
			WALLS = new HashMap<String, Wall>();
			List<Wall> walls = getWalls();
			for (Wall wall : walls) {
				PluginHolder pluginHolder = pluginService
						.getPluginHolderFromSoftwares(wall
								.getSoftwareAccesses());
				wall.setPluginHolder(pluginHolder);
				WALLS.put(wall.getName(), wall);
			}
			refreshWalls();
		}
	}

	public Wall find(String wallName) throws NotFoundException {
		Preconditions.checkNotNull(wallName, "wallName");

		return WALLS.get(wallName);
		// Wall wall = entityManager.find(Wall.class, wallName);
		// if (wall == null) {
		// throw new NotFoundException("Wall with name : " + wallName
		// + " not found in database");
		// }
		// return wall;
	}

	public void persist(Wall wall) throws NotCreatedException {
		Preconditions.checkNotNull(wall, "wall");
		try {
			entityManager.merge(wall);
			entityManager.flush();
		} catch (Throwable e) {
			String message = "Can't create wall " + wall + " in database";
			LOG.error(message, e);
			throw new NotCreatedException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Wall> getWalls() {
		Query query = entityManager.createNamedQuery(Wall.QUERY_WALLS);
		return query.getResultList();
	}

	public Set<String> getWallNames() {
		return WALLS.keySet();
	}

	@Scheduled(fixedDelay = EVERY_FIVE_MINUTES)
	public void refreshWalls() {
		if (LOG.isInfoEnabled()) {
			LOG.info("It's time to refresh all walls");
		}
		for (Wall wall : WALLS.values()) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Refreshing wall : " + wall + " and its "
						+ wall.getProjects().size() + " projects");
			}
			projectService.updateWallProjects(wall);
		}
	}

	public List<ProjectStatus> getStatus(Wall wall) {
		List<ProjectStatus> statusList = new ArrayList<ProjectStatus>();
		for (Project project : wall.getProjects()) {
			ProjectStatus status = new ProjectStatus();
			statusList.add(status);

			status.setBuilding(projectService.isBuilding(
					wall.getPluginHolder(), project.getProjectId()));
			status.setLastBuildId(projectService.getLastBuildNumber(
					wall.getPluginHolder(), project.getProjectId()));
			status.setName(project.getProjectId().getName());
			status.setState(projectService.getState(wall.getPluginHolder(),
					project.getProjectId()));
		}
		return statusList;
	}

}
