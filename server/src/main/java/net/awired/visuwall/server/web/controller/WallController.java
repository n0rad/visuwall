package net.awired.visuwall.server.web.controller;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import net.awired.visuwall.api.domain.ProjectStatus;
import net.awired.visuwall.bambooclient.Bamboo;
import net.awired.visuwall.plugin.bamboo.BambooPlugin;
import net.awired.visuwall.plugin.hudson.HudsonPlugin;
import net.awired.visuwall.plugin.sonar.SonarPlugin;
import net.awired.visuwall.server.domain.Software;
import net.awired.visuwall.server.domain.SoftwareAccess;
import net.awired.visuwall.server.domain.Wall;
import net.awired.visuwall.server.exception.NotCreatedException;
import net.awired.visuwall.server.exception.NotFoundException;
import net.awired.visuwall.server.service.WallService;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/wall")
@Scope("singleton")
public class WallController {

	private static final String WALL_JSP = "wall/wallForm";

	@Autowired
	private WallService wallService;

	@PostConstruct
	void init() throws NotCreatedException {
		Wall newwall = new Wall("orange-vallee");
		List<SoftwareAccess> softwareAccesses = newwall.getSoftwareAccesses();

//		softwareAccesses.add(new SoftwareAccess(new Software(HudsonPlugin.class
//				.getName(), 1.0f),
//				"http://10.2.40.60/lifeisbetteron/jenkins"));
//		softwareAccesses.add(new SoftwareAccess(new Software(HudsonPlugin.class
//				.getName(), 1.0f),
//				"http://integration.wormee.orange-vallee.net:8080/jenkins"));
//		softwareAccesses.add(new SoftwareAccess(new Software(SonarPlugin.class
//				.getName(), 1.0f),
//				"http://integration.wormee.orange-vallee.net:9000"));

		softwareAccesses.add(new SoftwareAccess(new Software(HudsonPlugin.class
				.getName(), 1.0f), "http://ci.awired.net/jenkins"));
		softwareAccesses.add(new SoftwareAccess(new Software(SonarPlugin.class
				.getName(), 1.0f), "http://sonar.awired.net"));
		softwareAccesses.add(new SoftwareAccess(new Software(HudsonPlugin.class
				.getName(), 1.0f), "http://ci.visuwall.awired.net"));
		// softwareAccesses.add(new SoftwareAccess(new
		// Software(BambooPlugin.class.getName(), 1.0f),
		// "http://bamboo.visuwall.awired.net"));

		wallService.persist(newwall);
	}

	@RequestMapping
	public String getWallNames(ModelMap modelMap) {
		Set<String> wallNames = wallService.getWallNames();
		modelMap.put("data", wallNames);
		return "wall/wallList";
	}

	@RequestMapping("{wallName}")
	public String getProjects(@PathVariable String wallName, ModelMap modelMap)
			throws NotFoundException {
		Wall wall = wallService.find(wallName);
		modelMap.put("data", wall);
		return WALL_JSP;
	}

	@RequestMapping("{wallName}/status")
	public @ResponseBody
	List<ProjectStatus> getStatus(@PathVariable String wallName,
			ModelMap modelMap) throws NotFoundException {
		Wall wall = wallService.find(wallName);
		List<ProjectStatus> status = wallService.getStatus(wall);
		return status;
	}

	@RequestMapping(value = "create", method = RequestMethod.GET)
	public String getCreate(ModelMap modelMap) {
		Wall wall = new Wall();
		modelMap.put("data", wall);
		return WALL_JSP;
	}

	@RequestMapping(value = "create", method = RequestMethod.POST)
	public ModelAndView create(Wall wall) throws NotCreatedException {
		wallService.persist(wall);
		return null;
	}

	@RequestMapping(value = "{wallName}", method = RequestMethod.DELETE)
	public void DeleteWall(@PathVariable String wallName) {
		throw new NotImplementedException();
	}

}