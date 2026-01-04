package com.visita.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.visita.dto.request.ReviewRequest;
import com.visita.dto.response.ReviewResponse;
import com.visita.entities.BookingStatus;
import com.visita.entities.ReviewEntity;
import com.visita.entities.TourEntity;
import com.visita.entities.UserEntity;
import com.visita.exceptions.ErrorCode;
import com.visita.exceptions.WebException;
import com.visita.repositories.BookingRepository;
import com.visita.repositories.ReviewRepository;
import com.visita.repositories.TourRepository;
import com.visita.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));

        TourEntity tour = tourRepository.findById(request.getTourId())
                .orElseThrow(() -> new WebException(ErrorCode.TOUR_NOT_FOUND));

        // 1. Check if User has gone on the Tour (Status = CONFIRMED)
        boolean hasConfirmedBooking = bookingRepository.existsByUser_UsernameAndTour_TourIdAndStatus(
                username, request.getTourId(), BookingStatus.CONFIRMED);

        if (!hasConfirmedBooking) {
            throw new RuntimeException("You can only review tours you have booked and are CONFIRMED.");
        }

        // 2. Prevent Duplicate Reviews
        if (reviewRepository.existsByUser_UserIdAndTour_TourId(user.getUserId(), request.getTourId())) {
            throw new RuntimeException("You have already reviewed this tour.");
        }

        ReviewEntity review = ReviewEntity.builder()
                .user(user)
                .tour(tour)
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
