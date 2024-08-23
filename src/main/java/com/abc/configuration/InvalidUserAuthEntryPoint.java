package com.abc.configuration;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class InvalidUserAuthEntryPoint implements AuthenticationEntryPoint {
	private static final Logger LOG = LoggerFactory.getLogger(InvalidUserAuthEntryPoint.class);

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		LOG.info("ERROR: InvalidUserAuthEntryPoint-{}", authException.getMessage());
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
				"ERROR: InvalidUserAuthEntryPoint-" + authException.getMessage());

	}

}
