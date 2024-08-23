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
public class RegisterRequest {

	private String name;
    private String email;
	private String password;
	private Set<String> roles;
}