package net.awired.visuwall.core.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.awired.visuwall.core.domain.Wall;
import net.awired.visuwall.core.exception.NotCreatedException;
import net.awired.visuwall.core.exception.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

@Repository
@Transactional
public class WallServiceImpl implements WallService {
    
    private static final Logger LOG = LoggerFactory.getLogger(WallServiceImpl.class);
	
	@PersistenceContext
    private EntityManager entityManager;
    
    public void persist(Wall wall) throws NotCreatedException {
        Preconditions.checkNotNull(wall, "wall parameter is mandatory");
        Preconditions.checkNotNull(wall.getName(), "wall must have a name");

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

    @VisibleForTesting
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


	@Override
	public Wall find(String wallName) throws NotFoundException {
		Wall wall = entityManager.find(Wall.class, wallName);
		return wall;
	}

}
