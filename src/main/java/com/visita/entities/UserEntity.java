package com.visita.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "full_name", nullable = false)
	private String fullName;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(length = 15)
	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "ENUM('Male','Female','Other')")
	private Gender gender;

	private LocalDate dob;

	@Column
	private String address;

	@Column(name = "is_active")
	private Boolean isActive; // BIT(1)

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<BookingEntity> bookings;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<ReviewEntity> reviews;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<ChatSessionEntity> chatSessions;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<HistoryEntity> histories;
}
