package com.abc.dto;

import java.util.Set;

import com.abc.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

	private String token;
	private Integer id;
	private String name;
	private String email;
	private Set<String> roles;

  
}