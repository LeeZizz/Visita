package com.visita.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "booking_id")
	private String bookingId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tour_id")
	private TourEntity tour;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "staff_id")
	private UserEntity staff;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "promotion_id")
	private PromotionEntity promotion;

	@Column(name = "booking_date")
	private LocalDateTime bookingDate;

	@Column(name = "num_adults", nullable = false)
	private Integer numAdults;

	@Column(name = "num_children")
	private Integer numChildren;

	@Column(name = "total_price", nullable = false, precision = 15, scale = 3)
	private BigDecimal totalPrice;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "ENUM('PENDING','CONFIRMED','CANCELLED','COMPLETED')")
	private BookingStatus status;

	@Column(name = "special_request", length = 500)
	private String specialRequest;

	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<InvoiceEntity> invoices;

	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<PaymentEntity> payments;
}
