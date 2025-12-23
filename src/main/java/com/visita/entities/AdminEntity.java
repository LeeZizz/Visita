package com.visita.entities;


import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "admin_id")
	private String adminId;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(name = "full_name", nullable = false)
	private String fullName;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
