package com.visita.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visita.dto.request.ReviewRequest;
import com.visita.dto.response.ApiResponse;
import com.visita.dto.response.ReviewResponse;
import com.visita.services.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ApiResponse<ReviewResponse> createReview(@RequestBody @Valid ReviewRequest request) {
        ApiResponse<ReviewResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(reviewService.createReview(request));
        return apiResponse;
    }

    @GetMapping("/tour/{tourId}")
    public ApiResponse<org.springframework.data.domain.Page<ReviewResponse>> getReviewsByTour(
            @PathVariable String tourId,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "1") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "5") int size) {
        ApiResponse<org.springframework.data.domain.Page<ReviewResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(reviewService.getReviewsByTour(tourId, page - 1, size));
        return apiResponse;
    }
}
