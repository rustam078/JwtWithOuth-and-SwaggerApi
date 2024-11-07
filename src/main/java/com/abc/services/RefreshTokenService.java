package com.abc.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.abc.entity.RefreshToken;
import com.abc.entity.UserEntity;
import com.abc.exception.InvalidJwtTokenException;
import com.abc.repo.RefreshTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh.expiration-ms}")
    private Long refreshTokenExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(UserEntity user) {
    	
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpirationMs))
                .user(user)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken);
    }

    @Transactional
    public void deleteByUser(UserEntity user) {
        refreshTokenRepository.deleteByUser(user);
    }
    
    public RefreshToken verifyRefreshToken(String token) {
    	RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token).orElseThrow(()-> new InvalidJwtTokenException("Refresh token is Invalid."));
         if(refreshToken.getExpiryDate().isBefore(Instant.now())){
        	 refreshTokenRepository.delete(refreshToken);
        	 throw new InvalidJwtTokenException("Invalid or expired refresh token");
         }else {
			return refreshToken;
		}
    }
}
