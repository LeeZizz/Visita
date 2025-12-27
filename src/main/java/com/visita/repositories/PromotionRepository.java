package com.visita.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.visita.entities.PromotionEntity;

@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity, String> {
    boolean existsByCode(String code);
}
