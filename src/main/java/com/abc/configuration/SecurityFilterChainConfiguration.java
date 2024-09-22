package com.abc.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.abc.filter.JwtAuthenticationFilter;
import com.abc.services.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityFilterChainConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final LogoutHandler logoutHandler;
	private final InvalidUserAuthEntryPoint invalidUserAuthEntryPoint;
	private final CustomOAuth2UserService customOAuth2UserService;

	 @Value("${ui.domain.path}")
	  private String uiPath;
	 
	 
	 
	 @Bean
	 public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	     return http.csrf(AbstractHttpConfigurer ::disable)
	         .authorizeHttpRequests(authorize -> authorize
	             .requestMatchers("/","/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/oauthSignin", "/user-info").permitAll()
	             .requestMatchers("/admin").hasAnyAuthority("ADMIN")
	             .requestMatchers("/user").hasAnyAuthority("ADMIN","USER")
	             .anyRequest().authenticated()
	         )
	         .oauth2Login(oauth2 -> oauth2
	             .defaultSuccessUrl(uiPath)
	             .userInfoEndpoint(userInfo -> userInfo
	                 .userService(customOAuth2UserService))) // Custom OAuth2 user service
	         .exceptionHandling(config -> config
	             .authenticationEntryPoint(invalidUserAuthEntryPoint))
//	         .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	         .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
	         .logout(logout -> logout
	             .logoutUrl("/api/logout")
	             .addLogoutHandler(logoutHandler)
	             .logoutSuccessHandler((request, response, authentication) -> 
	                 SecurityContextHolder.clearContext()))
	         .build();
	 }


}