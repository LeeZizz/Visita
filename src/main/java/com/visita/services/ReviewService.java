package com.visita.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.visita.dto.request.ReviewRequest;
import com.visita.dto.response.ReviewResponse;
import com.visita.entities.BookingStatus;
import com.visita.entities.BookingEntity;
import com.visita.entities.ReviewEntity;
import com.visita.entities.TourEntity;
import com.visita.entities.UserEntity;
import com.visita.exceptions.ErrorCode;
import com.visita.exceptions.WebException;
import com.visita.repositories.BookingRepository;
import com.visita.repositories.ReviewRepository;
import com.visita.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(name)
                .or(() -> userRepository.findByEmail(name))
                .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));

        // 1. Look up the booking
        BookingEntity booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new WebException(ErrorCode.BOOKING_NOT_FOUND));

        // 2. Validate booking belongs to the current user
        if (!booking.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("You can only review your own bookings.");
        }

        // 3. Validate booking status is COMPLETED
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new RuntimeException("You can only review completed bookings.");
        }

        // 4. Prevent duplicate reviews for the same booking
        if (reviewRepository.existsByBooking_BookingId(request.getBookingId())) {
            throw new RuntimeException("You have already reviewed this booking.");
        }

        TourEntity tour = booking.getTour();

        ReviewEntity review = ReviewEntity.builder()
                .user(user)
                .tour(tour)
                .booking(booking)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);
        return mapToResponse(review);
    }

    public List<ReviewResponse> getReviewsByTour(String tourId) {
        return reviewRepository.findByTour_TourIdOrderByCreatedAtDesc(tourId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public org.springframework.data.domain.Page<ReviewResponse> getReviewsByTour(String tourId, int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by("createdAt").descending());
        // Only return VISIBLE reviews for public API
        return reviewRepository.findByTour_TourIdAndIsVisibleTrue(tourId, pageable).map(this::mapToResponse);
    }

    public org.springframework.data.domain.Page<ReviewResponse> getAllReviews(int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by("createdAt").descending());
        return reviewRepository.findAll(pageable).map(this::mapToResponse);
    }

    public void toggleReviewVisibility(String reviewId, boolean isVisible) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new WebException(ErrorCode.UNKNOWN_ERROR));
        review.setIsVisible(isVisible);
        reviewRepository.save(review);
    }

    private ReviewResponse mapToResponse(ReviewEntity review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .bookingId(review.getBooking() != null ? review.getBooking().getBookingId() : null)
                .tourId(review.getTour().getTourId())
                .userId(review.getUser().getUserId())
                .userName(review.getUser().getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .isVisible(review.getIsVisible())
                .build();
    }
}
