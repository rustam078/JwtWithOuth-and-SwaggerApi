package com.abc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@GetMapping("/")
	public String allAccess() {
		System.out.println("TestController.allAccess()");
		return "Welcome to Spring Jwt Security! This Api can be access by all without Authenticate";
	}
	
	@GetMapping("/user")
	public String allUser() {
		System.out.println("TestController.allUser()");
		return " This Api can be access by User and Admin Role Only";
	}
	@GetMapping("/admin")
	public String allAdmin() {
		System.out.println("TestControoller.allAdmin()");
		return " This Api can be access only be Admin Api";
	}
	


}
