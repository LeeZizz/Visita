package com.visita.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class BookingDetailResponse {
    String bookingId;

    // User Info
    String userId;
    String userName; // Full name
    String userEmail;
    String userPhone;

    // Tour Info
    String tourId;
    String tourTitle;
    LocalDateTime startDate; // If tour has specific dates, or is it in booking? Booking usually follows tour
                             // dates
    // Actually, BookingEntity doesn't have separate start/end date, it links to
    // Tour.
    // But TourEntity has startDate/endDate.

    // Booking Info
    LocalDateTime bookingDate;
    Integer numAdults;
    Integer numChildren;
    BigDecimal totalPrice;
    String status;
    String specialRequest;

    // Payment Info
    String paymentMethod;
    String paymentStatus;
    BigDecimal paymentAmount;
    String transactionId;
    LocalDateTime paymentDate;

    // Promotion Info
    String promotionCode;
    BigDecimal discountAmount;
}
