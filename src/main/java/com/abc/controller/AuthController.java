package com.abc.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abc.dto.AuthenticationRequest;
import com.abc.dto.AuthenticationResponse;
import com.abc.dto.RegisterRequest;
import com.abc.entity.Token;
import com.abc.entity.UserEntity;
import com.abc.repo.TokenRepo;
import com.abc.services.AuthService;
import com.abc.utils.AppConstants;
import com.abc.utils.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
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
	private TokenRepo tokenRepository;

	@PostMapping("/register")
	public ResponseEntity<UserEntity> registerUser(@Valid @RequestBody RegisterRequest request) {
		UserEntity user = userService.registerUser(request);
		return ResponseEntity.ok(user);
	}
	
	@GetMapping("/test")
		public String hello() {
			return "Rustam";
		}
	

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest loginRequest) {
		Authentication authentication = null;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
		} catch (Exception e) {
			LOG.error("LOGIN ERROR : {}", e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = jwtUtils.generateToken(authentication);
		UserEntity user = userService.findByEmail(loginRequest.getEmail()).orElse(null);
		if (user == null)
			return new ResponseEntity<>("User not Found", HttpStatus.NOT_FOUND);

		userService.revokeAllUserValidTokens(user);
		saveToken(token, user);

		Set<String> roles = user.getRoles().stream().map(item -> item.getName().toString()).collect(Collectors.toSet());
		return new ResponseEntity<>(new AuthenticationResponse(AppConstants.BEARER + " " + token, user.getUserId(),
				user.getName(), user.getEmail(), roles), HttpStatus.OK);

	}

	@PostMapping("/logout")
	public ResponseEntity<String> signout(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer"))
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		String jwt = authHeader.substring(7);
		Token storedToken = userService.findByToken(jwt).orElse(null);
		if (storedToken != null) {
//			      storedToken.setExpired(true);
//			      storedToken.setRevoked(true);
//			      tokenRepository.save(storedToken);
			tokenRepository.deleteById(storedToken.getTokenId());
			SecurityContextHolder.clearContext();
			return new ResponseEntity<>("Logout Success!...", HttpStatus.CREATED);
		}

		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	private void saveToken(String token, UserEntity user) {
		Token tokenEntity = Token.builder().user(user).token(token).tokenType(AppConstants.BEARER).revoked(false)
				.expired(false).build();
		userService.saveOrUpdateToken(tokenEntity);
	}
}
