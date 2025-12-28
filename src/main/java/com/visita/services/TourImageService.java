package com.visita.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.visita.dto.request.TourImageRequest;
import com.visita.entities.TourEntity;
import com.visita.entities.TourImageEntity;
import com.visita.exceptions.ErrorCode;
import com.visita.exceptions.WebException;
import com.visita.repositories.TourImageRepository;
import com.visita.repositories.TourRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TourImageService {

    private final TourImageRepository tourImageRepository;
    private final TourRepository tourRepository;

    public TourImageEntity addImage(String tourId, TourImageRequest request) {
        TourEntity tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new WebException(ErrorCode.TOUR_NOT_FOUND));

        TourImageEntity image = TourImageEntity.builder()
                .tour(tour)
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .build();

        return tourImageRepository.save(image);
    }

    public List<TourImageEntity> getImagesByTour(String tourId) {
        if (!tourRepository.existsById(tourId)) {
            throw new WebException(ErrorCode.TOUR_NOT_FOUND);
        }
        return tourImageRepository.findByTour_TourId(tourId);
    }

    public void deleteImage(String tourId, String imageId) {
        if (!tourRepository.existsById(tourId)) {
            throw new WebException(ErrorCode.TOUR_NOT_FOUND);
        }

        TourImageEntity image = tourImageRepository.findById(imageId)
                .orElseThrow(() -> new WebException(ErrorCode.IMAGE_NOT_FOUND)); // Assuming IMAGE_NOT_FOUND exists,
                                                                                 // else use generic

        // ensure image belongs to tour
        if (!image.getTour().getTourId().equals(tourId)) {
            throw new WebException(ErrorCode.IMAGE_NOT_FOUND);
        }

        tourImageRepository.delete(image);
    }

    public TourImageEntity updateImage(String tourId, String imageId, TourImageRequest request) {
        if (!tourRepository.existsById(tourId)) {
            throw new WebException(ErrorCode.TOUR_NOT_FOUND);
        }

        TourImageEntity image = tourImageRepository.findById(imageId)
                .orElseThrow(() -> new WebException(ErrorCode.IMAGE_NOT_FOUND));

        // ensure image belongs to tour
        if (!image.getTour().getTourId().equals(tourId)) {
            throw new WebException(ErrorCode.IMAGE_NOT_FOUND);
        }

        image.setImageUrl(request.getImageUrl());
        image.setDescription(request.getDescription());

        return tourImageRepository.save(image);
    }
}
