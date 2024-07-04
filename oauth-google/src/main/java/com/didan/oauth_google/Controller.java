package com.didan.oauth_google;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
	
	@GetMapping("/")
	public Authentication get(Authentication authentication) {
		return authentication;
	}
}
