package com.visita.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tour_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourImageEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "image_id")
	private String imageId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tour_id")
	@com.fasterxml.jackson.annotation.JsonBackReference("tour-images")
	private TourEntity tour;

	@Column(name = "image_url", nullable = false, columnDefinition = "LONGTEXT")
	private String imageUrl;

	@Column
	private String description;
}
