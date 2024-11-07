package com.abc.dto;

import java.util.Set;

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
	private String refreshToken;
	private Long accessTokenExpiry; 
	private Integer id;
	private String name;
	private String email;
	private Set<String> roles;

  
}