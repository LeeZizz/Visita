package com.visita.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.visita.entities.TourEntity;

@Repository
public interface TourRepository extends JpaRepository<TourEntity, String> {
}
