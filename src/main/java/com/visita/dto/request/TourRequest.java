package com.visita.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TourRequest {

    @NotBlank(message = "Title is required")
    String title;

    @com.fasterxml.jackson.annotation.JsonProperty("staff_id")
    @NotBlank(message = "STAFF_ID_REQUIRED")
    String staffId;

    String description;
    String itinerary;

    @NotNull(message = "Price for adult is required")
    @Min(value = 0, message = "Price must be non-negative")
    BigDecimal priceAdult;

    @NotNull(message = "Price for child is required")
    @Min(value = 0, message = "Price must be non-negative")
    BigDecimal priceChild;

    String duration;

    @NotBlank(message = "Destination is required")
    String destination;

    @Future(message = "Start date must be in the future")
    LocalDate startDate;

    @Future(message = "End date must be in the future")
    LocalDate endDate;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    Integer capacity;

    Integer availability; // 1 for available, 0 for unavailable
}
