package com.ogae.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UserController {
	
	@GetMapping("/main.do")
	public String mainView() {
		log.debug(null);
		return "main";
	}

	@GetMapping("/about.do")
	public String aboutView(@ModelAttribute("tab") String tab) {
		log.debug("ModelAttribute tab: {}", tab);
		return "about/about";
	}
	

}
