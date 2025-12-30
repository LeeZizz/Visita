package com.visita.services.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.visita.dto.request.MoMoIPNRequest;

import com.visita.entities.PaymentEntity;
import com.visita.entities.PaymentStatus;
import com.visita.exceptions.ErrorCode;
import com.visita.exceptions.WebException;
import com.visita.repositories.BookingRepository;
import com.visita.repositories.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository; // Need this to update booking status
    private final PayPalService payPalService;

    @Transactional
    public com.visita.dto.response.PayPalPaymentResponse capturePayPalOrder(String orderId) {
        // 1. Capture on PayPal (Throws exception if failed)
        payPalService.capturePayment(orderId);

        // 2. Find Payment using transaction ID
        // In BookingService, we set transactionId = PayPal Order ID
        PaymentEntity payment = paymentRepository.findByTransactionId(orderId)
                .orElseThrow(() -> new WebException(ErrorCode.UNKNOWN_ERROR)); // Or PAYMENT_NOT_FOUND

        // 3. Update Status
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(java.time.LocalDateTime.now());
        paymentRepository.save(payment);

        return com.visita.dto.response.PayPalPaymentResponse.builder().status("COMPLETED").build();
    }

    @Transactional
    public void processMoMoIPN(MoMoIPNRequest request) {
        log.info("Processing MoMo IPN: {}", request);

        // 1. Verify Signature (Skip for now or implement if keys are available here)
        // In a real app, you MUST verify the signature from MoMo using your Secret Key.

        // 2. Check Result Code
        if (request.getResultCode() != 0) {
            log.warn("Payment failed for OrderId: {}, ResultCode: {}", request.getOrderId(), request.getResultCode());
            // Update payment status to FAILED if needed
            return;
        }

        // 3. Extract Booking ID from OrderID (bookingId_timestamp)
        String orderId = request.getOrderId();
        String extractedBookingId = orderId;
        if (orderId.contains("_")) {
            extractedBookingId = orderId.substring(0, orderId.lastIndexOf("_"));
        }
        final String finalBookingId = extractedBookingId;

        // 4. Find Booking and Payment
        // Since we don't have a direct "Application Payment ID" saved in MoMo request
        // (only orderId which is bookingId based),
        // we find the Payment via Booking.
        // Assuming 1 Booking has 1 Payment for simplicity, or find the PENDING one.

        if (!bookingRepository.existsById(finalBookingId)) {
            throw new WebException(ErrorCode.UNKNOWN_ERROR);
        }

        PaymentEntity payment = paymentRepository
                .findByBooking_BookingIdAndStatus(finalBookingId, PaymentStatus.PENDING)
                .orElseThrow(() -> new RuntimeException("No pending payment found for booking: " + finalBookingId));

        // 5. Update Payment
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(request.getTransId());
        payment.setPaymentDate(java.time.LocalDateTime.now());
        paymentRepository.save(payment);

        // 6. Update Booking
        // booking.setStatus(BookingStatus.CONFIRMED);
        // User requested to keep Booking Status as PENDING even after payment success.
        // So we only update Payment Status.
        // bookingRepository.save(booking);

        log.info("Payment successful for Booking: {}, TransactionID: {}", finalBookingId, request.getTransId());
    }
}
