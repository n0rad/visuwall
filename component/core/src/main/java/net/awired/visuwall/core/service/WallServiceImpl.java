/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package net.awired.visuwall.core.service;

import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import net.awired.visuwall.core.domain.SoftwareAccess;
import net.awired.visuwall.core.domain.Wall;
import net.awired.visuwall.core.exception.NotCreatedException;
import net.awired.visuwall.core.exception.NotFoundException;
import org.apache.commons.lang.NotImplementedException;
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

    @Override
    public void persist(Wall wall) throws NotCreatedException {
        Preconditions.checkNotNull(wall, "wall parameter is mandatory");
        Preconditions.checkNotNull(wall.getName(), "wall must have a name");

        try {
            entityManager.persist(wall);
        } catch (Throwable e) {
            String message = "Can't create wall " + wall + " in database";
            LOG.error(message, e);
            throw new NotCreatedException(message, e);
        }
    }

    @Override
    public Wall update(Wall wall) {
        Wall persistWall = entityManager.merge(wall);
        return persistWall;
    }

    @SuppressWarnings("unchecked")
    public List<Wall> getWalls() {
        Query query = entityManager.createNamedQuery(Wall.QUERY_WALLS);
        List<Wall> resultList = query.getResultList();

        for (Wall wall : resultList) {
            // TODO replace with lazy load with extended entityManager or eager request 
            for (SoftwareAccess softwareInfo : wall.getSoftwareAccesses()) {
                List<String> projectNames = softwareInfo.getProjectNames();
                for (String string : projectNames) {

                }
                List<String> projectViews = softwareInfo.getViewNames();
                for (String string : projectViews) {

                }
            }
        }
        return resultList;
    }

    @VisibleForTesting
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Wall find(String wallName) throws NotFoundException {
        Wall wall = entityManager.find(Wall.class, wallName);

        // TODO replace with lazy load with extended entityManager or eager request 
        for (SoftwareAccess softwareInfo : wall.getSoftwareAccesses()) {
            softwareInfo.getProjectNames();
            softwareInfo.getViewNames();
        }
        return wall;
    }

    @Override
    public Set<String> getWallNames() {
        throw new NotImplementedException();
    }

}
