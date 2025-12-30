package com.visita.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.visita.entities.ReviewEntity;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, String> {
    java.util.List<ReviewEntity> findByTour_TourIdOrderByCreatedAtDesc(String tourId);

    org.springframework.data.domain.Page<ReviewEntity> findByTour_TourId(String tourId,
            org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<ReviewEntity> findByTour_TourIdAndIsVisibleTrue(String tourId,
            org.springframework.data.domain.Pageable pageable);

    boolean existsByUser_UserIdAndTour_TourId(String userId, String tourId);
}
