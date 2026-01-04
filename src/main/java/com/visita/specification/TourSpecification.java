package com.visita.specification;

import com.visita.entities.ReviewEntity;
import com.visita.entities.TourEntity;
import com.visita.enums.TourCategory;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TourSpecification {

    public static Specification<TourEntity> filterTours(
            String title,
            String destination,
            TourCategory category,
            com.visita.enums.Region region,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            LocalDate startDateFrom,
            LocalDate endDateTo,
            LocalDate endDateLimit,
            Double minRating,
            Integer minCapacity) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter by isActive = true
            predicates.add(criteriaBuilder.isTrue(root.get("isActive")));

            // Title (Partial match, case insensitive)
            if (title != null && !title.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                        "%" + title.toLowerCase() + "%"));
            }

            // Destination (Partial match, case insensitive)
            if (destination != null && !destination.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("destination")),
                        "%" + destination.toLowerCase() + "%"));
            }

            // Category
            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }

            // Region
            if (region != null) {
                predicates.add(criteriaBuilder.equal(root.get("region"), region));
            }

            // Price Range (priceAdult)
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("priceAdult"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("priceAdult"), maxPrice));
            }

            // Date Range
            if (startDateFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDateFrom));
            }
            if (endDateTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), endDateTo));
            }
            // End Date Limit (Tour must end before or on this date)
            if (endDateLimit != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), endDateLimit));
            }

            // Min Capacity (Check if tour has at least X total capacity)
            // Filter by minimum capacity
            if (minCapacity != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("capacity"), minCapacity));
            }

            // Rating
            if (minRating != null) {
                // Subquery to calculate average rating
                Subquery<Double> avgRatingSubquery = query.subquery(Double.class);
                Root<ReviewEntity> reviewRoot = avgRatingSubquery.from(ReviewEntity.class);
                avgRatingSubquery.select(criteriaBuilder.avg(reviewRoot.get("rating").as(Double.class)));
                avgRatingSubquery.where(criteriaBuilder.equal(reviewRoot.get("tour"), root));

                // Filter tours where average rating >= minRating
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(avgRatingSubquery, minRating));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
