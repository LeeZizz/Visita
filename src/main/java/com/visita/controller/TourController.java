package com.visita.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.visita.dto.response.ApiResponse;
import com.visita.services.TourService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/tours")
@Slf4j
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    @GetMapping
    public ApiResponse<org.springframework.data.domain.Page<com.visita.dto.response.TourResponse>> getAllActiveTours(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) com.visita.enums.TourCategory category,
            @RequestParam(required = false) com.visita.enums.Region region,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDateFrom,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDateTo,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDateLimit,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer numAdults,
            @RequestParam(required = false) Integer numChildren,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        ApiResponse<org.springframework.data.domain.Page<com.visita.dto.response.TourResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(
                tourService.getAllActiveTours(page - 1, size, title, destination, category, region, minPrice, maxPrice,
                        startDateFrom,
                        endDateTo, endDateLimit, minRating, numAdults, numChildren, sortBy, sortDirection)
                        .map(tourService::mapToTourResponse));
        return apiResponse;
    }

    @GetMapping("/{id}")
    public ApiResponse<com.visita.dto.response.TourResponse> getTourById(@PathVariable String id) {
        ApiResponse<com.visita.dto.response.TourResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(tourService.mapToTourResponse(tourService.getTourById(id)));
        return apiResponse;
    }
}
