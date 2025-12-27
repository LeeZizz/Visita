package com.visita.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.visita.dto.request.PromotionRequest;
import com.visita.entities.PromotionEntity;
import com.visita.exceptions.ErrorCode;
import com.visita.exceptions.WebException;
import com.visita.repositories.PromotionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionEntity createPromotion(PromotionRequest request) {
        if (promotionRepository.existsByCode(request.getCode())) {
            throw new WebException(ErrorCode.PROMOTION_EXISTED);
        }

        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getStartDate().isAfter(request.getEndDate())) {
            throw new WebException(ErrorCode.END_DATE_AFTER_START_DATE);
        }

        PromotionEntity promotion = PromotionEntity.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .discountAmount(request.getDiscountAmount())
                .discountPercent(request.getDiscountPercent())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .quantity(request.getQuantity())
                .build();

        return promotionRepository.save(promotion);
    }

    public PromotionEntity updatePromotion(String id, PromotionRequest request) {
        PromotionEntity promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new WebException(ErrorCode.PROMOTION_NOT_FOUND));

        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getStartDate().isAfter(request.getEndDate())) {
            throw new WebException(ErrorCode.END_DATE_AFTER_START_DATE);
        }

        promotion.setCode(request.getCode());
        promotion.setDescription(request.getDescription());
        promotion.setDiscountAmount(request.getDiscountAmount());
        promotion.setDiscountPercent(request.getDiscountPercent());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setQuantity(request.getQuantity());

        return promotionRepository.save(promotion);
    }

    public void deletePromotion(String id) {
        if (!promotionRepository.existsById(id)) {
            throw new WebException(ErrorCode.PROMOTION_NOT_FOUND);
        }
        promotionRepository.deleteById(id);
    }

    public void updateStatus(String promotionId, boolean isActive) {
        PromotionEntity promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new WebException(ErrorCode.PROMOTION_NOT_FOUND));
        promotion.setIsActive(isActive);
        promotionRepository.save(promotion);
    }

    public List<PromotionEntity> getAllPromotions() {
        return promotionRepository.findAll();
    }
}
