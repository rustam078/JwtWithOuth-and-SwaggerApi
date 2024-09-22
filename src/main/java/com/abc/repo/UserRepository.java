package com.abc.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.abc.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
	boolean existsByEmail(String email);
	  @Query("SELECT u.isActive FROM UserEntity u WHERE u.email = :email")
	  Boolean findIsActiveByUsername(@Param("email") String email);
}
