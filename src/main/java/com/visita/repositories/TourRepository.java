package com.visita.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.visita.entities.TourEntity;

@Repository
public interface TourRepository
        extends JpaRepository<TourEntity, String>,
        org.springframework.data.jpa.repository.JpaSpecificationExecutor<TourEntity> {
    org.springframework.data.domain.Page<TourEntity> findAllByIsActiveTrue(
            org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<TourEntity> findByIsActiveTrueAndCategory(
            com.visita.enums.TourCategory category, org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<TourEntity> findByIsActiveTrueAndPriceAdultBetween(
            java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice,
            org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<TourEntity> findByIsActiveTrueAndCategoryAndPriceAdultBetween(
            com.visita.enums.TourCategory category, java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice,
            org.springframework.data.domain.Pageable pageable);
}
