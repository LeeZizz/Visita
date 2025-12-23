package com.visita.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentStatus {
    PENDING("Chờ xử lý"),
    SUCCESS("Thành công"),
    FAILED("Thất bại"),
    REFUNDED("Đã hoàn tiền");

    private final String displayName;
}
