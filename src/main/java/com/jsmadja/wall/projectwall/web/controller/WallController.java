package com.jsmadja.wall.projectwall.web.controller;

import javax.ws.rs.GET;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/wall")
public class WallController {

	@GET
	@RequestMapping
	public ModelAndView getWall() {
		return new ModelAndView("wall");
	}
}
