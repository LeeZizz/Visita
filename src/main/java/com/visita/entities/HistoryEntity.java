package com.visita.entities;

import java.time.LocalDateTime;

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
@Table(name = "history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "history_id")
	private String historyId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tour_id")
	private TourEntity tour;

	@Column(name = "action_type", length = 100)
	private String actionType; // VIEW, SEARCH, BOOK

	@Column(name = "timestamp")
	private LocalDateTime timestamp;
}
