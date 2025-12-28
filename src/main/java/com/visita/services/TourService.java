package com.visita.services;

import java.util.List;

import org.springframework.stereotype.Service;

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
                .category(request.getCategory()) // Map category
                .region(request.getRegion()) // Map region
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
        if (request.getCategory() != null) {
            tour.setCategory(request.getCategory());
        }
        if (request.getRegion() != null) {
            tour.setRegion(request.getRegion());
        }
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

    public org.springframework.data.domain.Page<TourEntity> getAllActiveTours(
            int page,
            int size,
            String title,
            String destination,
            com.visita.enums.TourCategory category,
            com.visita.enums.Region region,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            java.time.LocalDate startDateFrom,
            java.time.LocalDate endDateTo, // Start Date limit
            java.time.LocalDate endDateLimit, // End Date limit
            Double minRating,
            Integer numAdults,
            Integer numChildren,
            String sortBy,
            String sortDirection) {

        // Handle Sorting
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            org.springframework.data.domain.Sort.Direction direction = (sortDirection != null
                    && sortDirection.equalsIgnoreCase("desc"))
                            ? org.springframework.data.domain.Sort.Direction.DESC
                            : org.springframework.data.domain.Sort.Direction.ASC;

            if (sortBy.equalsIgnoreCase("price")) {
                sort = org.springframework.data.domain.Sort.by(direction, "priceAdult");
            } else if (sortBy.equalsIgnoreCase("duration")) {
                sort = org.springframework.data.domain.Sort.by(direction, "duration");
            } else if (sortBy.equalsIgnoreCase("startDate")) {
                sort = org.springframework.data.domain.Sort.by(direction, "startDate");
            }
        }

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                sort);

        // Calculate minCapacity required
        Integer minCapacity = null;
        if (numAdults != null || numChildren != null) {
            minCapacity = (numAdults != null ? numAdults : 0) + (numChildren != null ? numChildren : 0);
        }

        // Build Specification
        org.springframework.data.jpa.domain.Specification<TourEntity> spec = com.visita.specification.TourSpecification
                .filterTours(
                        title, destination, category, region, minPrice, maxPrice, startDateFrom, endDateTo,
                        endDateLimit,
                        minRating, minCapacity);

        return tourRepository.findAll(spec, pageable);
    }

    public TourEntity getTourById(String id) {
        TourEntity tour = tourRepository.findById(id)
                .orElseThrow(() -> new WebException(ErrorCode.TOUR_NOT_FOUND));
        if (!tour.getIsActive()) {
            throw new WebException(ErrorCode.TOUR_NOT_FOUND);
        }
        return tour;
    }

    public List<TourEntity> getAllTours() {
        return tourRepository.findAll();
    }
}
