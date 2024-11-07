package com.abc.dto;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

	@NotEmpty(message = "Name can not be a null or empty")
	@Size(min = 5, max = 30, message = "The length of the user name should be between 5 and 30")
	private String name;
	@NotEmpty(message = "Email address can not be a null or empty")
	@Email(message = "Email address should be a valid value")
	private String email;

//	@NotEmpty(message = "Password cannot be null or empty")
//	@Size(min = 8, message = "Password must be at least 8 characters long")
//	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
	private String password;

	@NotNull(message = "Roles cannot be null")
	@NotEmpty(message = "At least one role must be assigned")
	private Set<String> roles;
}