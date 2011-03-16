package com.jsmadja.wall.projectwall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/wall")
public class WallController {
	
	private static final String WALL_JSP = "wall/wall";

	@RequestMapping(value = "create", method=RequestMethod.GET)
	public String getCreate() {
		return WALL_JSP;
	}

	@RequestMapping(value = "create", method= RequestMethod.POST)
	public ModelAndView create() {
		return null;
	}

}
