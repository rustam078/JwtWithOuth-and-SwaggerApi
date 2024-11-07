package com.abc.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abc.entity.RefreshToken;
import com.abc.entity.UserEntity;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String token);
    void deleteByUser(UserEntity user);
}
