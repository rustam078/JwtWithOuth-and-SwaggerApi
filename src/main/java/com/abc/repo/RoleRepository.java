package com.abc.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abc.entity.ERole;
import com.abc.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}