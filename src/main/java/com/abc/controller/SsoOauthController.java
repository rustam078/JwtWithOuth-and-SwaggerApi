package com.abc.controller;


import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.abc.dto.AuthenticationResponse;
import com.abc.entity.ERole;
import com.abc.entity.Token;
import com.abc.entity.UserEntity;
import com.abc.repo.RoleRepository;
import com.abc.repo.UserRepository;
import com.abc.services.AuthService;
import com.abc.services.PasswordGeneratorService;
import com.abc.utils.AppConstants;
import com.abc.utils.JwtUtils;

@RestController
public class SsoOauthController {
    private static final Logger logger = LoggerFactory.getLogger(SsoOauthController.class);

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
	private AuthService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Lazy
    private PasswordEncoder encoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordGeneratorService passwordGeneratorService;

    @GetMapping("/loginGoogle")
    public RedirectView google() {
        return new RedirectView("/oauth2/authorization/google");
    }

    @GetMapping("/oauth2/callback1")
    public String oauth2Callback() {
        return "Login with Google successful!";
    }

    
    @GetMapping(value = "/oauthSignin", produces = "application/json")
    public ResponseEntity<?> authenticateUser(Principal principal) {
        // OAuth2 logic here
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2User oauthUser = ((OAuth2AuthenticationToken) principal).getPrincipal();
            String email = oauthUser.getAttribute("email");
            // Authenticate using the OAuth2 token
            String generatedPassword = passwordGeneratorService.generatePassword(12);
            saveOAuthUser(principal, generatedPassword);
            
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(email, generatedPassword));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            String jwt = jwtUtils.generateToken(authentication);
            // Send JWT token to the frontend for use in subsequent requests
          UserEntity user = userRepository.findByEmail(email)
          	    .orElseThrow(() -> new RuntimeException("No user found with email: " + email));
  		if (user == null)
  			return new ResponseEntity<>("User not Found", HttpStatus.NOT_FOUND);

  		userService.revokeAllUserValidTokens(user);
  		saveToken(jwt, user);

          Set<String> roles = user.getRoles().stream().map(item -> item.getName().toString()).collect(Collectors.toSet());
      	return new ResponseEntity<>(new AuthenticationResponse(AppConstants.BEARER + " " + jwt, user.getUserId(),
  				user.getName(), user.getEmail(), roles), HttpStatus.OK);
        }
        return ResponseEntity.badRequest().body("Invalid authentication.");
    }

    @GetMapping("/user-info")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttributes();
    }

    @Transactional
    public String saveOAuthUser(Principal principal, String generatedPassword) {
        String encodedPassword = encoder.encode(generatedPassword);
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2User oauthUser = ((OAuth2AuthenticationToken) principal).getPrincipal();
            String email = oauthUser.getAttribute("email");
            String username = email;

            Optional<UserEntity> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                UserEntity existingUser = userOptional.get();
                existingUser.setPassword(encodedPassword);
                userRepository.save(existingUser);
                logger.info("User updated: {}", existingUser.getEmail());
            } else {
                UserEntity newUser = new UserEntity();
                newUser.setEmail(username);
                newUser.setName(username);
                newUser.setPassword(encodedPassword);
                newUser.setActive(true);
                newUser.setRoles(Set.of(roleRepository.findByName(ERole.ADMIN)
                        .orElseThrow(() -> new RuntimeException("Role ADMIN not found"))));

                userRepository.save(newUser);
                logger.info("User saved: {}", newUser.getEmail());
            }
            return "User saved/updated successfully";
        }
        return "Invalid principal";
    }
    
	private void saveToken(String token, UserEntity user) {
		Token tokenEntity = Token.builder().user(user).token(token).tokenType(AppConstants.BEARER).revoked(false)
				.expired(false).build();
		userService.saveOrUpdateToken(tokenEntity);
	}
}
