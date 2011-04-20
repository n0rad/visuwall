package net.awired.visuwall.core.service;

import java.util.List;

import net.awired.visuwall.core.domain.Wall;
import net.awired.visuwall.core.exception.NotCreatedException;
import net.awired.visuwall.core.exception.NotFoundException;

public interface WallService {

	void persist(Wall wall) throws NotCreatedException;

	List<Wall> getWalls();

	Wall find(String wallName) throws NotFoundException;

}
