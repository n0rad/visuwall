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
import java.util.Set;
import net.awired.visuwall.core.exception.NotCreatedException;
import net.awired.visuwall.core.exception.NotFoundException;
import net.awired.visuwall.core.persistence.entity.Wall;

public interface WallDAO {

    void persist(Wall wall) throws NotCreatedException;

    List<Wall> getWalls();

    Set<String> getWallNames();

    Wall find(String wallName) throws NotFoundException;

    Wall update(Wall wall);

}
