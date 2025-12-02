package com.vitazi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vitazi.entities.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

	Optional<RoleEntity> findByRoleName(String roleName);
}
