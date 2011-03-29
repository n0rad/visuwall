package net.awired.visuwall.server.service;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.awired.visuwall.server.domain.Wall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

@Service
public class WallService {

    @PersistenceContext
    private EntityManager entityManager;

    private final static int EVERY_FIVE_MINUTES = 5*60*1000;

    private Set<Wall> walls = new HashSet<Wall>();

    private static final Logger LOG = LoggerFactory.getLogger(WallService.class);

    public WallService() {
        if (LOG.isInfoEnabled()) {
            LOG.info("WallService is starting ...");
        }
    }

    @Scheduled(fixedDelay=EVERY_FIVE_MINUTES)
    public synchronized void refreshWalls() {
        if (LOG.isInfoEnabled()) {
            LOG.info("It's time to refresh all walls");
        }
        for(Wall wall:walls) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Refreshing wall : "+wall+" and its "+wall.getProjects().size()+" projects");
            }
            wall.discoverProjects();
        }
    }

    public synchronized void addWall(Wall wall) {
        Preconditions.checkNotNull(wall);
        walls.add(wall);
    }

    @Transactional
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
