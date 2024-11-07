package com.abc.controller;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abc.dto.AuthenticationRequest;
import com.abc.dto.AuthenticationResponse;
import com.abc.dto.RefreshTokenDto;
import com.abc.dto.RegisterRequest;
import com.abc.entity.RefreshToken;
import com.abc.entity.UserEntity;
import com.abc.exception.InvalidCredentialsException;
import com.abc.exception.ResponseDto;
import com.abc.services.AuthService;
import com.abc.services.RefreshTokenService;
import com.abc.utils.AppConstants;
import com.abc.utils.JwtUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private AuthService userService;
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private RefreshTokenService refreshTokenService;

	@PostMapping("/register")
	public ResponseEntity<ResponseDto> registerUser(@Valid @RequestBody RegisterRequest request) {
		userService.registerUser(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ResponseDto(AppConstants.STATUS_201, AppConstants.MESSAGE_201));
	}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthenticationRequest loginRequest) {
		Authentication authentication = null;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
		} catch (Exception e) {
			LOG.error("LOGIN ERROR : {}", e.getMessage());
			throw new InvalidCredentialsException("Invalid email or password.");
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserEntity user = userService.findByEmail(loginRequest.getEmail()).orElse(null);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseDto(AppConstants.NOT_FOUND_404, "User Details not Found please SignUp First"));
		}

		String token = jwtUtils.generateToken(authentication);
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
		Date expirationDate = jwtUtils.extractExpiration(token);
		userService.revokeAllUserValidTokens(user);
		userService.saveToken(token, user);

		Set<String> roles = user.getRoles().stream().map(item -> item.getName().toString()).collect(Collectors.toSet());
		return new ResponseEntity<>(
				new AuthenticationResponse(AppConstants.BEARER + " " + token, refreshToken.getRefreshToken(),
						expirationDate.getTime(), user.getUserId(), user.getName(), user.getEmail(), roles),
				HttpStatus.OK);
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenDto refreshToken) {
		RefreshToken refreshTokenObj = refreshTokenService.verifyRefreshToken(refreshToken.getRefreshToken());
		UserEntity user = refreshTokenObj.getUser();
		String newAccessToken = jwtUtils.generateRefreshToken(user.getEmail());
		Date expirationDate = jwtUtils.extractExpiration(newAccessToken);
		userService.revokeAllUserValidTokens(user);
		userService.saveToken(newAccessToken, user);
		Set<String> roles = user.getRoles().stream().map(item -> item.getName().toString()).collect(Collectors.toSet());
		return ResponseEntity.ok(AuthenticationResponse.builder().token(AppConstants.BEARER + " " + newAccessToken)
				.refreshToken(refreshToken.getRefreshToken()).accessTokenExpiry(expirationDate.getTime())
				.id(user.getUserId()).name(user.getName()).email(user.getEmail()).roles(roles).build());
	}

}
