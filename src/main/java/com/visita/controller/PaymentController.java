package com.visita.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visita.dto.request.MoMoIPNRequest;
import com.visita.services.payment.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/ipn-momo")
    public ResponseEntity<Void> handleMoMoIPN(@RequestBody MoMoIPNRequest request) {
        try {
            paymentService.processMoMoIPN(request);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            log.error("Error processing IPN", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/capture-paypal")
    public ResponseEntity<?> capturePayPal(
            @org.springframework.web.bind.annotation.RequestParam("token") String token) {
        try {
            // Token in redirect is usually orderID or token
            com.visita.dto.response.PayPalPaymentResponse response = paymentService.capturePayPalOrder(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error capturing PayPal", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
