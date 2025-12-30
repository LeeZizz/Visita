package com.visita.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.visita.entities.PaymentEntity;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {
    java.util.Optional<PaymentEntity> findByBooking_BookingIdAndStatus(String bookingId,
            com.visita.entities.PaymentStatus status);

    java.util.Optional<PaymentEntity> findByTransactionId(String transactionId);
}
