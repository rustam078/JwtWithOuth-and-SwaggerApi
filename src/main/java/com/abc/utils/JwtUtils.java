package com.abc.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.abc.entity.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);
	private final String SECRET_KEY = "4bb6d1dfbafb64a681139d1586b6f1160d18159afd57c8c79136d7490630407c";

	@Value("${Jwt_Issuer}")
	private String issuer;



	public String generateToken(Authentication authentication) {
		UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
		return getToken(principal.getEmail());
	}


	private Claims getClaims(String token) {
		try {
			return Jwts
					.parser()
					.verifyWith(getSigninKey())
					.build()
					.parseSignedClaims(token)
					.getPayload();

		} catch (MalformedJwtException e) {
			LOGGER.error("token malformed!");
			return null;
		} catch (ExpiredJwtException e) {
			LOGGER.error("token expired!");
			e.printStackTrace();
			return null;
		} catch (UnsupportedJwtException e) {
			LOGGER.error("token unsupported!");
			return null;
		} catch (JwtException e) {
			LOGGER.error("Invalid signature!");
			return null;
		} catch (IllegalArgumentException e) {
			LOGGER.error("Claims String is empty!");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	

	public String getEmailFromToken(String token) {
		try {
			if (token != null && !token.isEmpty() && isValidToken(token)) {
				String subject = getClaims(token).getSubject();
				return subject;
			} else
				return null;
		} catch (ExpiredJwtException e) {
			LOGGER.error("ERROR:ExpiredJwtException Getting email from TOKEN, {} ", e.getMessage());
			return null;
		} catch (Exception e) {
			LOGGER.error("ERROR:Exception Getting email from TOKEN, {}", e.getMessage());
			return null;
		}
	}


	public boolean validateToken(String token) {
		try {
			return isValidToken(token);
		} catch (MalformedJwtException e) {
			LOGGER.error("token malformed!");
			return false;
		} catch (ExpiredJwtException e) {
			LOGGER.error("Token Expired!");
			e.printStackTrace();
			throw new RuntimeException("Token Expired. Please login again.");
		} catch (UnsupportedJwtException e) {
			LOGGER.error("token unsupported!");
			return false;
		} catch (JwtException e) {
			LOGGER.error("Invalid signature!");
			return false;
		} catch (IllegalArgumentException e) {
			LOGGER.error("Claims String is empty!");
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private String getToken(String subject) {
		return (subject == null || subject.isEmpty()) ? null
				: Jwts.builder()
				      .issuer(issuer)
				      .subject(subject)
				      .issuedAt(new Date(System.currentTimeMillis()))
					  .expiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(12)))
					  .signWith(getSigninKey())
					  .compact();
	}
	
	public boolean isValidToken(String token) {
		try {
			Claims claims = getClaims(token);
			return (claims != null && claims.getExpiration().after(new Date(System.currentTimeMillis())));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private SecretKey getSigninKey() {
		byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}

}
