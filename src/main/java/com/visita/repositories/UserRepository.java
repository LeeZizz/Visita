package com.visita.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.visita.entities.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
	Optional<UserEntity> findByEmail(String email);

	Optional<UserEntity> findByUsername(String username);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	org.springframework.data.domain.Page<UserEntity> findAllByRoles_Name(String role,
			org.springframework.data.domain.Pageable pageable);
}
