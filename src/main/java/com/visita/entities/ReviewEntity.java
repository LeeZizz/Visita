package com.visita.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "review_id")
	private String reviewId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tour_id")
	@com.fasterxml.jackson.annotation.JsonBackReference("tour-reviews")
	private TourEntity tour;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@com.fasterxml.jackson.annotation.JsonBackReference("user-reviews")
	private UserEntity user;

	@jakarta.validation.constraints.Min(value = 1, message = "Rating must be at least 1")
	@jakarta.validation.constraints.Max(value = 5, message = "Rating must be at most 5")
	private Integer rating; // 1–5

	@Lob
	private String comment;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "is_visible")
	@Builder.Default
	private Boolean isVisible = true;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		if (this.isVisible == null) {
			this.isVisible = true;
		}
	}
}
