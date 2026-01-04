package com.visita.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.visita.dto.request.BookingRequest;
import com.visita.dto.response.BookingResponse;
import com.visita.entities.BookingEntity;
import com.visita.entities.BookingStatus;
import com.visita.entities.PaymentEntity;
import com.visita.entities.PaymentStatus;
import com.visita.entities.PromotionEntity;
import com.visita.entities.TourEntity;
import com.visita.enums.PaymentMethod;
import com.visita.exceptions.ErrorCode;
import com.visita.exceptions.WebException;
import com.visita.repositories.BookingRepository;
import com.visita.repositories.PaymentRepository;
import com.visita.repositories.PromotionRepository;
import com.visita.repositories.TourRepository;
import com.visita.repositories.UserRepository;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private static final int MAX_RETRY_ATTEMPTS = 3;

    private final BookingRepository bookingRepository;
    private final TourRepository tourRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;
    private final com.visita.services.payment.MoMoService moMoService;
    private final com.visita.services.payment.PayPalService payPalService;
    private final com.visita.config.FrontendConfig frontendConfig;
    private final TransactionTemplate transactionTemplate;

    /**
     * Creates a booking with retry logic for handling optimistic locking conflicts.
     * If a concurrent update is detected (e.g., another user booked the last slot),
     * the operation is retried with fresh data up to MAX_RETRY_ATTEMPTS times.
     */
    public BookingResponse createBooking(BookingRequest request) {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                return transactionTemplate.execute(status -> doCreateBooking(request));
            } catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
                log.warn("Optimistic lock conflict on booking attempt {}/{}: {}",
                        attempt, MAX_RETRY_ATTEMPTS, e.getMessage());
                if (attempt == MAX_RETRY_ATTEMPTS) {
                    throw new WebException(ErrorCode.CONCURRENT_UPDATE);
                }
                // Continue to next retry attempt with fresh data
            }
        }
        // Should never reach here, but compiler needs this
        throw new WebException(ErrorCode.CONCURRENT_UPDATE);
    }

    /**
     * Internal method containing the actual booking creation logic.
     * Called within a transaction by the retry wrapper.
     */
    private BookingResponse doCreateBooking(BookingRequest request) {
        // 1. Get Current User
        var context = org.springframework.security.core.context.SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        com.visita.entities.UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));

        // 2. Validate Tour
        TourEntity tour = tourRepository.findById(request.getTourId())
                .orElseThrow(() -> new WebException(ErrorCode.TOUR_NOT_FOUND));

        if (!tour.getIsActive()) {
            throw new WebException(ErrorCode.TOUR_NOT_FOUND);
        }

        // Check Availability/Capacity?
        // Simple capacity check: Booking count vs Capacity. (Can be enhanced)
        // For now, assuming availability = 1 means available.
        if (tour.getAvailability() != null && tour.getAvailability() == 0) {
            throw new WebException(ErrorCode.TOUR_UNAVAILABLE);
        }

        // 3. Calculate Price
        int adults = request.getNumAdults();
        int children = request.getNumChildren() != null ? request.getNumChildren() : 0;

        BigDecimal priceAdult = tour.getPriceAdult();
        BigDecimal priceChild = tour.getPriceChild();

        BigDecimal originalPrice = priceAdult.multiply(BigDecimal.valueOf(adults))
                .add(priceChild.multiply(BigDecimal.valueOf(children)));

        // 4. Handle Promotion
        BigDecimal discountAmount = BigDecimal.ZERO;
        PromotionEntity promotion = null;

        if (request.getPromotionCode() != null && !request.getPromotionCode().isEmpty()) {
            promotion = promotionRepository.findByCode(request.getPromotionCode())
                    .orElseThrow(() -> new WebException(ErrorCode.PROMOTION_NOT_FOUND));

            // Validate Promotion
            if (!promotion.getIsActive()) {
                throw new WebException(ErrorCode.PROMOTION_INACTIVE);
            }
            if (promotion.getQuantity() <= 0) {
                throw new WebException(ErrorCode.PROMOTION_OUT_OF_STOCK);
            }
            LocalDate now = LocalDate.now();
            if (now.isBefore(promotion.getStartDate()) || now.isAfter(promotion.getEndDate())) {
                throw new WebException(ErrorCode.PROMOTION_EXPIRED);
            }

            // Calculate Discount
            if (promotion.getDiscountAmount() != null) {
                discountAmount = promotion.getDiscountAmount();
            } else if (promotion.getDiscountPercent() != null) {
                discountAmount = originalPrice.multiply(promotion.getDiscountPercent())
                        .divide(BigDecimal.valueOf(100));
            }

            // Decrease Quantity
            promotion.setQuantity(promotion.getQuantity() - 1);
            promotionRepository.save(promotion);
        }

        BigDecimal finalPrice = originalPrice.subtract(discountAmount);
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        // 5. Create Booking
        BookingEntity booking = BookingEntity.builder()
                .user(user)
                .tour(tour)
                .staff(tour.getStaff())
                .promotion(promotion)
                .bookingDate(LocalDateTime.now())
                .numAdults(adults)
                .numChildren(children)
                .totalPrice(finalPrice)
                .status(BookingStatus.PENDING)
                .specialRequest(request.getSpecialRequest())
                .build();

        booking = bookingRepository.save(booking);

        // 6. Create Payment
        PaymentEntity payment = PaymentEntity.builder()
                .booking(booking)
                .amount(finalPrice)
                .paymentMethod(request.getPaymentMethod().name()) // Enum to String
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        // 7. Generate Payment URL
        String paymentUrl = null;
        String message = "Booking created successfully. Please wait for confirmation.";

        if (request.getPaymentMethod() == PaymentMethod.MOMO) {
            paymentUrl = moMoService.createPayment(booking.getBookingId(), "Booking Tour: " + tour.getTitle(),
                    finalPrice);
            message = "Please pay via MoMo.";
        } else if (request.getPaymentMethod() == PaymentMethod.PAYPAL) {
            String returnUrl = frontendConfig.getUrl() + "/payment/paypal-success";
            String cancelUrl = frontendConfig.getUrl() + "/payment/paypal-cancel";
            com.visita.dto.response.PayPalPaymentResponse payPalResponse = payPalService.createPayment(finalPrice,
                    "USD", returnUrl, cancelUrl);
            paymentUrl = payPalResponse.getApproveLink();

            // Save PayPal Order ID as Transaction ID for later capture matching
            payment.setTransactionId(payPalResponse.getId());
            paymentRepository.save(payment);

            message = "Please pay via PayPal.";
        } else {
            message = "Please pay directly upon arrival/meeting.";
        }

        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .status(booking.getStatus().name())
                .originalPrice(originalPrice)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .paymentUrl(paymentUrl)
                .message(message)
                .build();
    }

    /**
     * Creates a booking for a user (staff workflow) with retry logic for handling
     * optimistic locking conflicts.
     */
    public BookingResponse createBookingForUser(com.visita.dto.request.StaffBookingRequest request) {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                return transactionTemplate.execute(status -> doCreateBookingForUser(request));
            } catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
                log.warn("Optimistic lock conflict on staff booking attempt {}/{}: {}",
                        attempt, MAX_RETRY_ATTEMPTS, e.getMessage());
                if (attempt == MAX_RETRY_ATTEMPTS) {
                    throw new WebException(ErrorCode.CONCURRENT_UPDATE);
                }
            }
        }
        throw new WebException(ErrorCode.CONCURRENT_UPDATE);
    }

    /**
     * Internal method containing the actual staff booking creation logic.
     */
    private BookingResponse doCreateBookingForUser(com.visita.dto.request.StaffBookingRequest request) {
        // 1. Get User
        com.visita.entities.UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));

        // 2. Validate Tour
        TourEntity tour = tourRepository.findById(request.getTourId())
                .orElseThrow(() -> new WebException(ErrorCode.TOUR_NOT_FOUND));

        if (!tour.getIsActive()) {
            throw new WebException(ErrorCode.TOUR_NOT_FOUND);
        }

        // 3. Calculate Price
        int adults = request.getNumAdults();
        int children = request.getNumChildren() != null ? request.getNumChildren() : 0;

        BigDecimal priceAdult = tour.getPriceAdult();
        BigDecimal priceChild = tour.getPriceChild();

        BigDecimal originalPrice = priceAdult.multiply(BigDecimal.valueOf(adults))
                .add(priceChild.multiply(BigDecimal.valueOf(children)));

        // 4. Handle Promotion
        BigDecimal discountAmount = BigDecimal.ZERO;
        PromotionEntity promotion = null;

        if (request.getPromotionCode() != null && !request.getPromotionCode().isEmpty()) {
            promotion = promotionRepository.findByCode(request.getPromotionCode())
                    .orElseThrow(() -> new WebException(ErrorCode.PROMOTION_NOT_FOUND));

            if (!promotion.getIsActive() || promotion.getQuantity() <= 0) {
                throw new WebException(ErrorCode.PROMOTION_UNAVAILABLE); // Generic validation
            }
            LocalDate now = LocalDate.now();
            if (now.isBefore(promotion.getStartDate()) || now.isAfter(promotion.getEndDate())) {
                throw new WebException(ErrorCode.PROMOTION_EXPIRED);
            }

            if (promotion.getDiscountAmount() != null) {
                discountAmount = promotion.getDiscountAmount();
            } else if (promotion.getDiscountPercent() != null) {
                discountAmount = originalPrice.multiply(promotion.getDiscountPercent())
                        .divide(BigDecimal.valueOf(100));
            }

            promotion.setQuantity(promotion.getQuantity() - 1);
            promotionRepository.save(promotion);
        }

        BigDecimal finalPrice = originalPrice.subtract(discountAmount);
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        // 5. Create Booking (Status CONFIRMED)
        BookingEntity booking = BookingEntity.builder()
                .user(user)
                .tour(tour)
                .staff(tour.getStaff())
                .promotion(promotion)
                .bookingDate(LocalDateTime.now())
                .numAdults(adults)
                .numChildren(children)
                .totalPrice(finalPrice)
                .status(BookingStatus.CONFIRMED)
                .specialRequest(request.getSpecialRequest())
                .build();

        booking = bookingRepository.save(booking);

        // 6. Create Payment (Method CASH, Status PENDING)
        PaymentEntity payment = PaymentEntity.builder()
                .booking(booking)
                .amount(finalPrice)
                .paymentMethod(PaymentMethod.CASH.name())
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .status(booking.getStatus().name())
                .originalPrice(originalPrice)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .message("Booking created successfully for user. Payment method: CASH.")
                .build();
    }
    // --- Admin Methods ---

    public org.springframework.data.domain.Page<com.visita.dto.response.BookingDetailResponse> getAllBookings(int page,
            int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by("bookingDate").descending());

        return bookingRepository.findAll(pageable).map(this::mapToDetailResponse);
    }

    public org.springframework.data.domain.Page<com.visita.dto.response.BookingDetailResponse> searchBookings(
            String keyword, int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by("bookingDate").descending());

        return bookingRepository.searchBookings(keyword, pageable).map(this::mapToDetailResponse);
    }

    public com.visita.dto.response.BookingDetailResponse getBookingById(String id) {
        BookingEntity booking = bookingRepository.findById(id)
                .orElseThrow(() -> new WebException(ErrorCode.UNKNOWN_ERROR)); // Should Create BOOKING_NOT_FOUND code
        return mapToDetailResponse(booking);
    }

    @Transactional
    public com.visita.dto.response.BookingDetailResponse updateBooking(String id,
            com.visita.dto.request.BookingUpdateRequest request) {
        BookingEntity booking = bookingRepository.findById(id)
                .orElseThrow(() -> new WebException(ErrorCode.UNKNOWN_ERROR));

        PaymentEntity payment = booking.getPayments() != null && !booking.getPayments().isEmpty()
                ? booking.getPayments().get(0)
                : null;
        boolean isPaymentPending = payment == null || payment.getStatus() == PaymentStatus.PENDING;

        if (request.getSpecialRequest() != null) {
            booking.setSpecialRequest(request.getSpecialRequest());
        }

        if (isPaymentPending) {
            boolean participantsChanged = false;

            if (request.getNumAdults() != null) {
                booking.setNumAdults(request.getNumAdults());
                participantsChanged = true;
            }
            if (request.getNumChildren() != null) {
                booking.setNumChildren(request.getNumChildren());
                participantsChanged = true;
            }

            if (participantsChanged) {
                recalculatePrice(booking);
            }

            if (request.getStatus() != null) {
                try {
                    booking.setStatus(BookingStatus.valueOf(request.getStatus().toUpperCase()));
                } catch (IllegalArgumentException e) {
                }
            }
        }

        return mapToDetailResponse(bookingRepository.save(booking));
    }

    @Transactional
    public void updateStatus(String id, String status) {
        BookingEntity booking = bookingRepository.findById(id)
                .orElseThrow(() -> new WebException(ErrorCode.UNKNOWN_ERROR));

        BookingStatus newStatus;
        try {
            newStatus = BookingStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Booking Status");
        }

        if (newStatus == BookingStatus.COMPLETED) {
            PaymentEntity payment = booking.getPayments() != null && !booking.getPayments().isEmpty()
                    ? booking.getPayments().get(0)
                    : null;
            if (payment == null || payment.getStatus() != PaymentStatus.SUCCESS) {
                throw new RuntimeException("Cannot change to COMPLETED: Payment is not SUCCESS.");
            }
        }

        booking.setStatus(newStatus);
        bookingRepository.save(booking);
    }

    private void recalculatePrice(BookingEntity booking) {
        BigDecimal priceAdult = booking.getTour().getPriceAdult();
        BigDecimal priceChild = booking.getTour().getPriceChild();

        int adults = booking.getNumAdults() != null ? booking.getNumAdults() : 0;
        int children = booking.getNumChildren() != null ? booking.getNumChildren() : 0;

        BigDecimal originalPrice = priceAdult.multiply(BigDecimal.valueOf(adults))
                .add(priceChild.multiply(BigDecimal.valueOf(children)));

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (booking.getPromotion() != null) {
            PromotionEntity promo = booking.getPromotion();
            if (promo.getDiscountAmount() != null) {
                discountAmount = promo.getDiscountAmount();
            } else if (promo.getDiscountPercent() != null) {
                discountAmount = originalPrice.multiply(promo.getDiscountPercent()).divide(BigDecimal.valueOf(100));
            }
        }

        BigDecimal finalPrice = originalPrice.subtract(discountAmount);
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0)
            finalPrice = BigDecimal.ZERO;

        booking.setTotalPrice(finalPrice);

        if (booking.getPayments() != null && !booking.getPayments().isEmpty()) {
            PaymentEntity payment = booking.getPayments().get(0);
            if (payment.getStatus() == PaymentStatus.PENDING) {
                payment.setAmount(finalPrice);
                paymentRepository.save(payment);
            }
        }
    }

    private com.visita.dto.response.BookingDetailResponse mapToDetailResponse(BookingEntity booking) {
        PaymentEntity payment = booking.getPayments() != null && !booking.getPayments().isEmpty()
                ? booking.getPayments().get(0)
                : null;

        return com.visita.dto.response.BookingDetailResponse.builder()
                .bookingId(booking.getBookingId())
                .userId(booking.getUser() != null ? booking.getUser().getUserId() : null)
                .userName(booking.getUser() != null ? booking.getUser().getFullName() : null)
                .userEmail(booking.getUser() != null ? booking.getUser().getEmail() : null)
                .userPhone(booking.getUser() != null ? booking.getUser().getPhone() : null)
                .tourId(booking.getTour() != null ? booking.getTour().getTourId() : null)
                .tourTitle(booking.getTour() != null ? booking.getTour().getTitle() : null)
                .startDate(booking.getTour() != null && booking.getTour().getStartDate() != null
                        ? booking.getTour().getStartDate().atStartOfDay()
                        : null)
                .bookingDate(booking.getBookingDate())
                .numAdults(booking.getNumAdults())
                .numChildren(booking.getNumChildren())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().name())
                .specialRequest(booking.getSpecialRequest())
                .paymentMethod(payment != null ? payment.getPaymentMethod() : "N/A")
                .paymentStatus(payment != null ? payment.getStatus().name() : "N/A")
                .paymentAmount(payment != null ? payment.getAmount() : BigDecimal.ZERO)
                .transactionId(payment != null ? payment.getTransactionId() : null)
                .paymentDate(payment != null ? payment.getPaymentDate() : null)
                .promotionCode(booking.getPromotion() != null ? booking.getPromotion().getCode() : null)
                .discountAmount(booking.getPromotion() != null ? calculateDiscount(booking) : BigDecimal.ZERO)
                .build();
    }

    private BigDecimal calculateDiscount(BookingEntity booking) {
        if (booking.getPromotion() == null)
            return BigDecimal.ZERO;
        // Simple logic for display, re-calculation from original not stored directly
        // But we have totalPrice = original - discount.
        // We can't perfectly reconstruct without original price stored,
        // but for now let's just return 0 or try to calc if original price is needed.
        // Actually BookingResponse has originalPrice, but Entity doesn't store it
        // explicitly ONLY totalPrice.
        // So we might skip discountAmount in DetailResponse for now or Estimate it.
        return BigDecimal.ZERO;
    }

    // --- User History Methods ---

    public org.springframework.data.domain.Page<com.visita.dto.response.BookingDetailResponse> getMyActiveBookings(
            int page, int size) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by("bookingDate").descending());

        return bookingRepository.findByUser_UsernameAndStatusNot(username, BookingStatus.COMPLETED, pageable)
                .map(this::mapToDetailResponse);
    }

    public org.springframework.data.domain.Page<com.visita.dto.response.BookingDetailResponse> getMyCompletedBookings(
            int page, int size) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by("bookingDate").descending());

        return bookingRepository.findByUser_UsernameAndStatus(username, BookingStatus.COMPLETED, pageable)
                .map(this::mapToDetailResponse);
    }

    public org.springframework.data.domain.Page<com.visita.dto.response.BookingDetailResponse> getBookingsByStaffId(
            String staffId, int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by("bookingDate").descending());

        return bookingRepository.findByStaff_UserId(staffId, pageable)
                .map(this::mapToDetailResponse);
    }
}
