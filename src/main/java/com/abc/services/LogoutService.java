package com.abc.services;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.abc.repo.TokenRepo;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final TokenRepo tokenRepository;

  @Override
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    jwt = authHeader.substring(7);
    var storedToken = tokenRepository.findByToken(jwt)
        .orElse(null);
    if (storedToken != null) {
//      storedToken.setExpired(true);
//      storedToken.setRevoked(true);
//      tokenRepository.save(storedToken);
    	tokenRepository.deleteById(storedToken.getTokenId());
      SecurityContextHolder.clearContext();
    }
    try {
		new ObjectMapper().writeValue(response.getOutputStream(), "Logout Success!...");
	} catch (StreamWriteException e) {
		e.printStackTrace();
	} catch (DatabindException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
  }
}