package com.vitazi.entities;

import java.time.LocalDate;

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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id", nullable = false)
	private RoleEntity role;

	@Column(nullable = false, unique = true)
	private String userName;

	@Column(nullable = false)
	private String passWord;

	private String fullName;

	@Column(nullable = false, length = 100)
	private String email;

	@Column(unique = true, nullable = false, length = 15)
	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Gender gender;

	private LocalDate createdAt = LocalDate.now();
	private LocalDate updatedAt = LocalDate.now();
	private boolean isActive;
	private LocalDate dob;
}
