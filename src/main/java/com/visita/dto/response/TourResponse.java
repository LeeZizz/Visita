package com.visita.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.visita.enums.Region;
import com.visita.enums.TourCategory;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TourResponse {

    private String tourId;
    private String title;
    private String description;
    private String itinerary;
    private BigDecimal priceAdult;
    private BigDecimal priceChild;
    private String duration;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer capacity;
    private Boolean isActive;
    private TourCategory category;
    private Region region;
    private Integer availability;
    private List<String> images; // Only URLs
    private Double averageRating;
    private Long reviewCount;
    private String staffId;
    private String staffName;
    private List<ReviewResponse> reviews;
}
