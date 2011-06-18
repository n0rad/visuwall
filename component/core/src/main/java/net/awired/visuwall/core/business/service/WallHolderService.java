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

package net.awired.visuwall.core.business.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import net.awired.visuwall.core.business.process.WallProcess;
import net.awired.visuwall.core.exception.NotFoundException;
import net.awired.visuwall.core.persistence.dao.WallDAO;
import net.awired.visuwall.core.persistence.entity.Wall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

@Service
public class WallHolderService implements WallDAO {

    private static final Logger LOG = LoggerFactory.getLogger(WallHolderService.class);

    @Autowired
    WallProcess wallProcess;

    @Autowired
    WallDAO wallDAO;

    @Autowired
    BuildProjectService buildProjectService;

    static Map<String, Wall> WALLS;

    @PostConstruct
    void init() {
        if (WALLS == null) {
            WALLS = new HashMap<String, Wall>();
            List<Wall> walls = wallDAO.getWalls();
            for (Wall wall : walls) {
                try {
                    wallProcess.rebuildFullWallInformations(wall);
                    WALLS.put(wall.getName(), wall);
                } catch (Exception e) {
                    LOG.error("can not reconstruct wall with name " + wall.getName(), e);
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<Wall> getWalls() {
        return new ArrayList<Wall>(WALLS.values());
    }

    @Override
    public Wall find(String wallName) throws NotFoundException {
        Preconditions.checkNotNull(wallName, "wallName");
        Wall wall = WALLS.get(wallName);
        if (wall == null) {
            throw new NotFoundException("Wall with name : " + wallName + " not found");
        }
        return wall;
    }

    @Override
    public Wall update(Wall wall) {
        Wall newWall = wallDAO.update(wall);
        wallProcess.rebuildFullWallInformations(newWall);
        Wall previousWall = WALLS.get(wall.getName());
        if (previousWall != null) {
            previousWall.close();
            WALLS.remove(previousWall.getName());
        }
        WALLS.put(newWall.getName(), newWall);
        return newWall;
    }

    public Set<String> getWallNames() {
        return WALLS.keySet();
    }

    @Override
    public void deleteWall(String wallName) {
        LOG.info("Removing wall : " + wallName);
        try {
            Wall wall = find(wallName);
            wall.close();
            WALLS.remove(wallName);
            wallDAO.deleteWall(wallName);
        } catch (NotFoundException e) {
            LOG.warn("No wall found to delete : " + wallName);
        }
    }

}
