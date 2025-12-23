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

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<BookingEntity> bookings;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<ReviewEntity> reviews;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<ChatSessionEntity> chatSessions;

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
