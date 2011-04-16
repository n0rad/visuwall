package net.awired.visuwall.server.web.controller;

import java.util.Collection;
import java.util.List;

import net.awired.visuwall.api.domain.ProjectStatus;
import net.awired.visuwall.api.plugin.Plugin;
import net.awired.visuwall.server.domain.Software;
import net.awired.visuwall.server.domain.Wall;
import net.awired.visuwall.server.exception.NotCreatedException;
import net.awired.visuwall.server.exception.NotFoundException;
import net.awired.visuwall.server.service.PluginService;
import net.awired.visuwall.server.service.SoftwareService;
import net.awired.visuwall.server.service.WallService;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/wall")
public class WallController {

	private static final Logger LOG = LoggerFactory
			.getLogger(WallController.class.getName());
	private static final String WALL_JSP = "wall/wallForm";

	@Autowired
	private WallService wallService;
	
	@Autowired
	private SoftwareService softwareService;

	@Autowired
	private PluginService pluginService;
	
	@ModelAttribute("softwares")
	public Collection<Software> populatePetTypes() {
		return softwareService.findAll();
	}


	public WallController() {
//		wall = new Wall("orange-vallee");
//		 wall.addSoftwareAccess(new SoftwareAccess(Software.HUDSON,
//		 "http://integration.wormee.orange-vallee.net:8080/hudson"));
//		 wall.addSoftwareAccess(new SoftwareAccess(Software.SONAR,
//		 "http://integration.wormee.orange-vallee.net:9000"));
//		wall.addSoftwareAccess(new SoftwareAccess(Software.HUDSON,
//				"http://ci.awired.net/jenkins"));
//		 wall.addSoftwareAccess(new SoftwareAccess(Software.HUDSON,
//		 "http://ci.visuwall.awired.net"));
//		wall.addSoftwareAccess(new SoftwareAccess(Software.SONAR,
//				"http://sonar.awired.net"));
//		 wall.addSoftwareAccess(new SoftwareAccess(Software.HUDSON,
//		 "http://fluxx.fr.cr:8080/hudson"));
//		 wall.addSoftwareAccess(new SoftwareAccess(Software.SONAR,
//		 "http://fluxx.fr.cr:9000"));
//		wall.discoverProjects();
	}

	// //////////////////////////////////////////////////////////////////

	@RequestMapping
	public @ResponseBody
	List<String> getWallNames() {
		List<Plugin> plugins = pluginService.getPlugins();
		for (Plugin plugin : plugins) {
			System.out.println(plugin);
		}
		return wallService.getWallNames();
	}

	@RequestMapping("{wallName}")
	public String getProjects(@PathVariable String wallName, ModelMap modelMap) throws NotFoundException {
		Wall wall = wallService.find(wallName);
		modelMap.put("data", wall);
		return WALL_JSP;
	}

	@RequestMapping("{wallName}/status")
	public @ResponseBody
	List<ProjectStatus> getStatus(@PathVariable String wallName) {
		throw new NotImplementedException();
		//return wallService.getStatus(wallName);
	}

	@RequestMapping(value = "create", method = RequestMethod.GET)
	public String getCreate(ModelMap modelMap) {
		Wall wall = new Wall();
//		SoftwareAccess softwareAccess = new SoftwareAccess();
//		softwareAccess.setSoftware(new Software());
//		wall.addSoftwareAccess(softwareAccess);
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

	}

}