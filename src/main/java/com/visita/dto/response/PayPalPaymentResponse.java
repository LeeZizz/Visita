package com.visita.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayPalPaymentResponse {
    private String id; // Order ID
    private String status;
    private String approveLink;
}
