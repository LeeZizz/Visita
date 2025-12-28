package com.visita.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visita.dto.request.TourImageRequest;
import com.visita.dto.response.ApiResponse;
import com.visita.entities.TourImageEntity;
import com.visita.services.TourImageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admins/tours")
@RequiredArgsConstructor
public class TourImageController {

    private final TourImageService tourImageService;

    @PostMapping("/{tourId}/images")
    public ApiResponse<TourImageEntity> addImage(@PathVariable String tourId,
            @RequestBody @Valid TourImageRequest request) {
        ApiResponse<TourImageEntity> apiResponse = new ApiResponse<>();
        apiResponse.setResult(tourImageService.addImage(tourId, request));
        return apiResponse;
    }

    @GetMapping("/{tourId}/images")
    public ApiResponse<List<TourImageEntity>> getImages(@PathVariable String tourId) {
        ApiResponse<List<TourImageEntity>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(tourImageService.getImagesByTour(tourId));
        return apiResponse;
    }

    @DeleteMapping("/{tourId}/images/{imageId}")
    public ApiResponse<String> deleteImage(@PathVariable String tourId, @PathVariable String imageId) {
        tourImageService.deleteImage(tourId, imageId);
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setResult("Image deleted successfully");
        return apiResponse;
    }

    @PutMapping("/{tourId}/images/{imageId}")
    public ApiResponse<TourImageEntity> updateImage(@PathVariable String tourId, @PathVariable String imageId,
            @RequestBody @Valid TourImageRequest request) {
        ApiResponse<TourImageEntity> apiResponse = new ApiResponse<>();
        apiResponse.setResult(tourImageService.updateImage(tourId, imageId, request));
        return apiResponse;
    }
}
