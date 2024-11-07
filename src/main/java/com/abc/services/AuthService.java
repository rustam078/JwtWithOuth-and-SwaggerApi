package com.abc.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abc.dto.RegisterRequest;
import com.abc.entity.ERole;
import com.abc.entity.Role;
import com.abc.entity.Token;
import com.abc.entity.UserEntity;
import com.abc.exception.CustomerAlreadyExistsException;
import com.abc.exception.ResourceNotFoundException;
import com.abc.repo.RoleRepository;
import com.abc.repo.TokenRepo;
import com.abc.repo.UserRepository;
import com.abc.utils.AppConstants;

@Service
public class AuthService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private TokenRepo tokenRepo;

	
	@Transactional
	public UserEntity registerUser(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new CustomerAlreadyExistsException(request.getEmail() +" Email already in use.");
		}

		Set<Role> roles = new HashSet<>();
		for (String roleName : request.getRoles()) {
			try {
				ERole eRole = ERole.valueOf(roleName.toUpperCase());
				Role role = roleRepository.findByName(eRole).orElseThrow(() -> new ResourceNotFoundException("Role","role",roleName));
				roles.add(role);
			} catch (IllegalArgumentException e) {
				throw new ResourceNotFoundException("Role","role",roleName);
			}
		}

		UserEntity userEntity = UserEntity.builder().name(request.getName()).email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword())).isActive(true).roles(roles).build();

		return userRepository.save(userEntity);
	}


	public Optional<UserEntity> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	

	public Token saveOrUpdateToken(Token token) {
		try {
			return tokenRepo.save(token);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Optional<Token> findByToken(String token) {
		try {
			return tokenRepo.findByToken(token);
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	public void revokeAllUserValidTokens(UserEntity user) {
		try {
			var validUserTokens = tokenRepo.findAllValidTokensByUser(user.getUserId());
			if (validUserTokens.isEmpty())
				return;
			validUserTokens.forEach(token -> {
				token.setExpired(true);
				token.setRevoked(true);
			});
			tokenRepo.saveAll(validUserTokens);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void saveToken(String token, UserEntity user) {
			Token tokenEntity = Token.builder().user(user).token(token).tokenType(AppConstants.BEARER).revoked(false)
					.expired(false).build();
			this.saveOrUpdateToken(tokenEntity);
	}
}