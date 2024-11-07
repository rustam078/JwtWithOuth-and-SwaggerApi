package com.abc.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.abc.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityFilterChainConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final LogoutHandler logoutHandler;
	private final InvalidUserAuthEntryPoint invalidUserAuthEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer ::disable)
            .authorizeHttpRequests(authorize -> authorize
            		.requestMatchers("/","/api/auth/**" ,"/v2/api-docs","/v2/api-docs/**", "/v3/api-docs", "/v3/api-docs/**","/v3/api-docs.yaml",  "/swagger-resources",
            			    "/swagger-resources/**", "/configuration/ui", "/configuration/security", "/swagger-ui/**","/swagger-ui.html","/swagger-ui/index.html",
            			    "/webjars/**", "/swagger-ui.html","/api-docs/**","/forgetPassword/**","/updatePassword").permitAll()
                .requestMatchers("/admin").hasAnyAuthority("ADMIN") 
                .requestMatchers("/user").hasAnyAuthority("ADMIN","USER") 
                .anyRequest().authenticated()
            )
         // Exception details
		     .exceptionHandling(config->config.authenticationEntryPoint(invalidUserAuthEntryPoint))
		     .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// register filter for 2nd request onwards

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter before UsernamePasswordAuthenticationFilter
			.logout(logout->logout.logoutUrl("/api/auth/logout")
                .addLogoutHandler(logoutHandler) 
                .logoutSuccessHandler((request, response, authentication) ->
                    SecurityContextHolder.clearContext() 
                ))
            .build();
    }
}