package com.visita.dto.response;

import java.math.BigDecimal;

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
public class BookingResponse {
    String bookingId;
    String status; // Booking Status
    String paymentUrl; // For online payment

    BigDecimal originalPrice;
    BigDecimal discountAmount;
    BigDecimal finalPrice;

    String message;
}
