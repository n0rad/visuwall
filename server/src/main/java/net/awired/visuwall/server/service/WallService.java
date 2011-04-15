package net.awired.visuwall.server.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.awired.visuwall.server.domain.Wall;
import net.awired.visuwall.server.exception.NotCreatedException;
import net.awired.visuwall.server.exception.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

@Repository
public class WallService {

    private static final Logger LOG = LoggerFactory.getLogger(WallService.class);

    @PersistenceContext
    private EntityManager entityManager;

    public Wall find(String wallName) throws NotFoundException {
        Preconditions.checkNotNull(wallName, "wallName");

        Wall wall = entityManager.find(Wall.class, wallName);
        if (wall == null) {
            throw new NotFoundException("Wall with name : " + wallName + " not found in database");
        }
        return wall;
    }

    @Transactional
    public void persist(Wall wall) throws NotCreatedException {
        Preconditions.checkNotNull(wall, "wall");
        try {
            entityManager.persist(wall);
            entityManager.flush();
        } catch(Throwable e) {
            String message = "Can't create wall " + wall + " in database";
            LOG.error(message, e);
            throw new NotCreatedException(message, e);
        }
    }
    
    public List<String> getWallNames() {
        Query query =  entityManager.createNamedQuery(Wall.QUERY_NAMES);
        return query.getResultList();
    }

}
