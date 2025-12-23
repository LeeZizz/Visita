package com.visita.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "payment_id")
	private String paymentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booking_id")
	private BookingEntity booking;

	@Column(name = "transaction_id", length = 100)
	private String transactionId;

	@Column(name = "payment_method", length = 50)
	private String paymentMethod;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	@Column(name = "payment_date")
	private LocalDateTime paymentDate;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "ENUM('PENDING','SUCCESS','FAILED','REFUNDED')")
	private PaymentStatus status;

    @PrePersist
    protected void onCreate() {
        this.paymentDate = LocalDateTime.now();
    }
}
