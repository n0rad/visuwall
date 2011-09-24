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

package net.awired.visuwall.core.persistence.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import net.awired.visuwall.core.exception.NotFoundException;
import net.awired.visuwall.core.persistence.entity.SoftwareAccess;
import net.awired.visuwall.core.persistence.entity.Wall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class WallDAOImpl implements WallDAO {

    private static final Logger LOG = LoggerFactory.getLogger(WallDAOImpl.class);

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Wall update(Wall wall) {
        Wall persistWall = entityManager.merge(wall);
        return persistWall;
    }

    @Override
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
                
                Map<String, String> properties = softwareInfo.getProperties();
                for (String string : properties.keySet()) {

                }                
            }
        }
        return resultList;
    }

    @Override
    public Wall find(String wallName) throws NotFoundException {
        Query query = entityManager.createNamedQuery(Wall.QUERY_WALLBYNAME);
        query.setParameter(Wall.QUERY_PARAM_NAME, wallName);
        Wall wall = (Wall) query.getSingleResult();

        // TODO replace with lazy load with extended entityManager or eager request 
        for (SoftwareAccess softwareInfo : wall.getSoftwareAccesses()) {
            softwareInfo.getProjectNames();
            softwareInfo.getViewNames();
        }
        return wall;
    }

    @Override
    public void deleteWall(String wallName) {
        Wall wall;
        try {
            wall = find(wallName);
            entityManager.remove(wall);
        } catch (NotFoundException e) {
            LOG.warn("No wall found in the DB to delete : " + wallName);
        }
    }

}
