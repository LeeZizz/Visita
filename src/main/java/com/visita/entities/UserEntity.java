package com.visita.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
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
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "user_id")
	private String userId;

	@Column(name = "full_name", nullable = false)
	private String fullName;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(length = 15)
	private String phone;

	@Column(unique = true, length = 50)
	private String username;

	@lombok.ToString.Exclude
	@lombok.EqualsAndHashCode.Exclude
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_name"))
	private java.util.Set<RoleEntity> roles;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "ENUM('MALE','FEMALE','OTHER')")
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

	@lombok.ToString.Exclude
	@lombok.EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<BookingEntity> bookings;

	@lombok.ToString.Exclude
	@lombok.EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<ReviewEntity> reviews;

	@lombok.ToString.Exclude
	@lombok.EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<ChatSessionEntity> chatSessions;

	@lombok.ToString.Exclude
	@lombok.EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<HistoryEntity> histories;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
