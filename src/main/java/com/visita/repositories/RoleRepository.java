package com.visita.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.visita.entities.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, String> {
}
