package com.visita.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tours")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tour_id")
	private Integer tourId;

	@Column(nullable = false)
	private String title;

	@Lob
	private String description; // MEDIUMTEXT

	@Lob
	private String itinerary; // LONGTEXT

	@Column(name = "price_adult", nullable = false, precision = 15, scale = 2)
	private BigDecimal priceAdult;

	@Column(name = "price_child", nullable = false, precision = 15, scale = 2)
	private BigDecimal priceChild;

	@Column(length = 50)
	private String duration; // "3 ngày 2 đêm"

	@Column
	private String destination;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(nullable = false)
	private Integer capacity;

	@Column
	private Integer availability; // 1 / 0

	@OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<TourImageEntity> images;

	@OneToMany(mappedBy = "tour", fetch = FetchType.LAZY)
	private List<BookingEntity> bookings;

	@OneToMany(mappedBy = "tour", fetch = FetchType.LAZY)
	private List<ReviewEntity> reviews;

	@OneToMany(mappedBy = "tour", fetch = FetchType.LAZY)
	private List<HistoryEntity> histories;
}
