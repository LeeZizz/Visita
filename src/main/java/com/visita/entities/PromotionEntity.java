package com.visita.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "promotion_id")
	private Integer promotionId;

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

	private Integer quantity;

	@OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY)
	private List<BookingEntity> bookings;
}
