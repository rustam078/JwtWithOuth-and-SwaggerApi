package com.abc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abc.entity.UserEntity;
import com.abc.utils.JwtUtils;

@RestController
public class TestController {

	@Autowired
	private JwtUtils jwtUtils;
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
	
	@GetMapping("/getLoggedUser")
	public Object getLoggedUser() {
		System.out.println("TestController.getLoggedUser()");
		return jwtUtils.getLoggedUserDetails();
	}
	


}
