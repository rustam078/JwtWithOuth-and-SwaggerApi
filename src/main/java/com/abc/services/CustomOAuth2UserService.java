package com.abc.services;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abc.entity.ERole;
import com.abc.entity.UserEntity;
import com.abc.repo.RoleRepository;
import com.abc.repo.UserRepository;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordGeneratorService passwordGeneratorService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Load the OAuth2 user using the DefaultOAuth2UserService
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Extract the email and name from the OAuth2User
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Process user registration and token generation logic
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            return updateExistingUser(userOptional.get(), oAuth2User);
        } else {
            return registerNewUser(email, name, oAuth2User);
        }
    }

    @Transactional
    private OAuth2User registerNewUser(String email, String name, OAuth2User oAuth2User) {
        String generatedPassword = passwordGeneratorService.generatePassword(12);

        // Encode the password and create a new UserEntity
        UserEntity newUser = new UserEntity();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setPassword(passwordEncoder.encode(generatedPassword));
        newUser.setActive(true);
        newUser.setRoles(Set.of(roleRepository.findByName(ERole.ADMIN)
            .orElseThrow(() -> new RuntimeException("Role USER not found"))));

        userRepository.save(newUser);

        // Return a new DefaultOAuth2User with authorities
        return new DefaultOAuth2User(
            Set.of(new SimpleGrantedAuthority(ERole.ADMIN.toString())),
            oAuth2User.getAttributes(),
            "email" // The key for the principal's name (e.g., "email" or "sub" for Google)
        );
    }

    @Transactional
    private OAuth2User updateExistingUser(UserEntity existingUser, OAuth2User oAuth2User) {
        // Optional logic for updating user attributes
        existingUser.setName(oAuth2User.getAttribute("name"));
        userRepository.save(existingUser);

        // Return the updated OAuth2User with authorities
        return new DefaultOAuth2User(
            Set.of(new SimpleGrantedAuthority(ERole.ADMIN.toString())),
            oAuth2User.getAttributes(),
            "email" // The key for the principal's name
        );
    }
}
