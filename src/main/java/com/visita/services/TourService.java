package com.visita.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.visita.dto.request.TourRequest;
import com.visita.entities.TourEntity;
import com.visita.exceptions.ErrorCode;
import com.visita.exceptions.WebException;
import com.visita.repositories.TourRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TourService {

    private final TourRepository tourRepository;
    private final com.visita.repositories.UserRepository userRepository;

    public TourEntity createTour(TourRequest request) {
        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getStartDate().isAfter(request.getEndDate())) {
            throw new WebException(ErrorCode.END_DATE_AFTER_START_DATE);
        }

        com.visita.entities.UserEntity staff = userRepository.findById(request.getStaffId())
                .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));

        TourEntity tour = TourEntity.builder()
                .title(request.getTitle())
                .staff(staff) // Assign staff
                .description(request.getDescription())
                .itinerary(request.getItinerary())
                .priceAdult(request.getPriceAdult())
                .priceChild(request.getPriceChild())
                .duration(request.getDuration())
                .destination(request.getDestination())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .capacity(request.getCapacity())
                .availability(request.getAvailability() != null ? request.getAvailability() : 1)
                .build();

        return tourRepository.save(tour);
    }

    public TourEntity updateTour(String id, TourRequest request) {
        TourEntity tour = tourRepository.findById(id)
                .orElseThrow(() -> new WebException(ErrorCode.TOUR_NOT_FOUND));

        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getStartDate().isAfter(request.getEndDate())) {
            throw new WebException(ErrorCode.END_DATE_AFTER_START_DATE);
        }

        tour.setTitle(request.getTitle());
        tour.setDescription(request.getDescription());
        tour.setItinerary(request.getItinerary());
        tour.setPriceAdult(request.getPriceAdult());
        tour.setPriceChild(request.getPriceChild());
        tour.setDuration(request.getDuration());
        tour.setDestination(request.getDestination());
        tour.setStartDate(request.getStartDate());
        tour.setEndDate(request.getEndDate());
        tour.setCapacity(request.getCapacity());
        if (request.getAvailability() != null) {
            tour.setAvailability(request.getAvailability());
        }

        return tourRepository.save(tour);
    }

    public void deleteTour(String id) {
        if (!tourRepository.existsById(id)) {
            throw new WebException(ErrorCode.TOUR_NOT_FOUND);
        }
        tourRepository.deleteById(id);
    }

    public void updateStatus(String tourId, boolean isActive) {
        TourEntity tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new WebException(ErrorCode.TOUR_NOT_FOUND));
        tour.setIsActive(isActive);
        tourRepository.save(tour);
    }

    public List<TourEntity> getAllTours() {
        return tourRepository.findAll();
    }
}
