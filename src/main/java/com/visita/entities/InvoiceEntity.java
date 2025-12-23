package com.visita.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "invoice_id")
	private String invoiceId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booking_id")
	private BookingEntity booking;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	@Column(name = "issued_date")
	private LocalDate issuedDate;

	@Column(length = 500)
	private String details; // JSON or text

    @PrePersist
    protected void onCreate() {
        this.issuedDate = LocalDate.now();
    }
}
