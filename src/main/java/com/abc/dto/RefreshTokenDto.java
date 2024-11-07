package com.abc.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RefreshTokenDto {

	@NotEmpty(message = "refreshToken cannot be null or empty")
	private String refreshToken;
}
