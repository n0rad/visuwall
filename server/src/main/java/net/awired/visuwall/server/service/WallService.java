package net.awired.visuwall.server.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.awired.visuwall.api.domain.ProjectStatus;
import net.awired.visuwall.server.domain.Wall;
import net.awired.visuwall.server.exception.WallNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import com.google.common.base.Preconditions;

@Repository
public class WallService {

    private static final Logger LOG = LoggerFactory.getLogger(WallService.class);

    private final static int EVERY_FIVE_MINUTES = 5*60*1000;

    @PersistenceContext
    private EntityManager entityManager;

    private static Map<String, Wall> WALLS = new HashMap<String, Wall>();

    public WallService() {
        if (LOG.isInfoEnabled()) {
            LOG.info("WallService is starting ...");
        }
    }

    @Scheduled(fixedDelay=EVERY_FIVE_MINUTES)
    public void refreshWalls() {
        if (LOG.isInfoEnabled()) {
            LOG.info("It's time to refresh all walls");
        }
        for(Wall wall:WALLS.values()) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Refreshing wall : "+wall+" and its "+wall.getProjects().size()+" projects");
            }
            wall.discoverProjects();
        }
    }

    public List<ProjectStatus> getStatus(String wallName) {
        Wall wall = WALLS.get(wallName);
        List<ProjectStatus> projectStatus  = wall.getStatus();
        return projectStatus;
    }

    public Wall getWall(String wallName) {
        return WALLS.get(wallName);
    }

    public void addWall(Wall wall) {
        Preconditions.checkNotNull(wall);
        WALLS.put(wall.getName(), wall);
    }

    public Set<String> getWallNames() {
        return WALLS.keySet();
    }

    //@Transactional
    public void persist(Wall wall) {
        entityManager.persist(wall);
        entityManager.flush();
    }

    public Wall load(String wallName) throws WallNotFoundException {
        Wall wall = entityManager.find(Wall.class, wallName);
        if (wall == null) {
            throw new WallNotFoundException("Wall with name:"+wallName+" has not been found in database");
        }
        wall.restoreServices();
        wall.discoverProjects();
        return wall;
    }
}
