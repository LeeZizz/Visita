package com.visita.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.visita.entities.TourImageEntity;
import java.util.List;

@Repository
public interface TourImageRepository extends JpaRepository<TourImageEntity, String> {
    List<TourImageEntity> findByTour_TourId(String tourId);
}
