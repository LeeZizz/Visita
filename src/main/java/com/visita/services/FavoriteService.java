package com.visita.services;

import com.visita.entities.FavoriteEntity;
import com.visita.entities.TourEntity;
import com.visita.entities.UserEntity;
import com.visita.exceptions.ErrorCode;
import com.visita.exceptions.WebException;
import com.visita.repositories.FavoriteRepository;
import com.visita.repositories.TourRepository;
import com.visita.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    private UserEntity getCurrentUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(name)
                .or(() -> userRepository.findByEmail(name))
                .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void addFavorite(String tourId) {
        UserEntity user = getCurrentUser();

        TourEntity tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new WebException(ErrorCode.TOUR_NOT_FOUND));

        if (favoriteRepository.existsByUser_UserIdAndTour_TourId(user.getUserId(), tourId)) {
            // Already favorite, maybe throw exception or just ignore
            // Let's ignore to be idempotent, or throw if specific requirement?
            // Typically idempotent is nice, but if user clicked "Heart", it should be
            // filled.
            return;
        }

        FavoriteEntity favorite = FavoriteEntity.builder()
                .user(user)
                .tour(tour)
                .build();

        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(String tourId) {
        UserEntity user = getCurrentUser();

        // We can just use the query to delete
        // But need to ensure it exists if we want to return 404?
        // User asked "api xóa favorite tour". Idempotent delete is usually fine.

        FavoriteEntity favorite = favoriteRepository.findByUser_UserIdAndTour_TourId(user.getUserId(), tourId)
                .orElseThrow(() -> new WebException(ErrorCode.TOUR_NOT_FOUND)); // Or FAVORITE_NOT_FOUND

        favoriteRepository.delete(favorite);
    }

    public List<TourEntity> getMyFavorites() {
        UserEntity user = getCurrentUser();
        List<FavoriteEntity> favorites = favoriteRepository.findByUser_UserId(user.getUserId());
        return favorites.stream()
                .map(FavoriteEntity::getTour)
                .collect(Collectors.toList());
    }
}
