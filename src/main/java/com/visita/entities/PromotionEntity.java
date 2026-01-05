package com.visita.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "promotion_id")
	private String promotionId;

	@Column(unique = true, length = 50)
	private String code;

	@Column
	private String description;

	@Column(name = "discount_amount", precision = 15, scale = 2)
	private BigDecimal discountAmount;

	@Column(name = "discount_percent", precision = 5, scale = 2)
	private BigDecimal discountPercent;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "quantity")
	private Integer quantity;

	@Column(name = "is_active", nullable = false)
	@Builder.Default
	private Boolean isActive = true;

	@OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY)
	@com.fasterxml.jackson.annotation.JsonManagedReference("promotion-bookings")
	private List<BookingEntity> bookings;

	@Version
	@Column(name = "version")
	private Long version;
}
