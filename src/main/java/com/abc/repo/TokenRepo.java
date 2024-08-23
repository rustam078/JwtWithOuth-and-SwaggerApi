package com.abc.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.abc.entity.Token;
import com.abc.entity.UserEntity;

public interface TokenRepo extends JpaRepository<Token, Integer> {

	@Query("select t from Token t inner join UserEntity u on t.user.userId  = u.userId "
			+ "where u.userId = :userId and (t.expired = false or t.revoked = false)")
	List<Token> findAllValidTokensByUser(Integer userId);

	Optional<Token> findByToken(String token);

	List<Token> findByUser(UserEntity user);
}
